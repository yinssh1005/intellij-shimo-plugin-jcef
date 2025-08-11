package i.s.p.j.intellijshimopluginjcef.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefClient
import i.s.p.j.intellijshimopluginjcef.file.ShimoFileType
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefLifeSpanHandlerAdapter
import org.cef.handler.CefRequestHandlerAdapter
import org.cef.handler.CefDownloadHandler
import org.cef.callback.CefBeforeDownloadCallback
import org.cef.callback.CefDownloadItemCallback
import org.cef.network.CefRequest
import org.cef.callback.CefDownloadItem
import javax.swing.JComponent
import i.s.p.j.intellijshimopluginjcef.settings.ShimoSettingsState
import java.io.File

/**
 * Panel that hosts a JCEF browser for displaying Shimo documents.
 */
class ShimoBrowserPanel(private val project: Project, private val url: String = DEFAULT_URL) : Disposable {
    companion object {
        // Key for storing the URL in the virtual file's user data
        val URL_KEY = Key.create<String>("SHIMO_URL")

        // Default URL for Shimo
        const val DEFAULT_URL = "https://shimo.im/desktop"
    }

    private val logger = Logger.getInstance(ShimoBrowserPanel::class.java)
    private val browser: JBCefBrowser = JBCefBrowser()

    init {
        logger.info("Initializing Shimo browser panel with URL: $url")

        // Install handlers to open links in a new IDE editor tab
        installOpenInNewEditorTabHandlers(browser.jbCefClient)
        // Install download handler to auto-save files to configured directory
        installDownloadHandler(browser.jbCefClient)

        // Load initial URL
        browser.loadURL(url)
    }

    private fun installOpenInNewEditorTabHandlers(client: JBCefClient) {
        // Intercept popups (target=_blank, window.open) and open them in a new editor tab instead
        client.addLifeSpanHandler(
            object : CefLifeSpanHandlerAdapter() {
                override fun onBeforePopup(
                    browser: CefBrowser?,
                    frame: CefFrame?,
                    target_url: String?,
                    target_frame_name: String?
                ): Boolean {
                    val url = target_url ?: return false
                    if (isHttpUrl(url)) {
                        logger.info("Opening popup URL in new editor tab: $url")
                        openInNewEditorTab(url)
                        // returning true cancels the popup creation in JCEF
                        return true
                    }
                    return false
                }
            },
            browser.cefBrowser
        )

        // Intercept normal navigations triggered by user gestures and open them in new editor tab
        client.addRequestHandler(
            object : CefRequestHandlerAdapter() {
                override fun onBeforeBrowse(
                    browser: CefBrowser?,
                    frame: CefFrame?,
                    request: CefRequest?,
                    user_gesture: Boolean,
                    is_redirect: Boolean
                ): Boolean {
                    val url = request?.url ?: return false
                    if (user_gesture && isHttpUrl(url)) {
                        logger.info("Intercepted navigation to open in new editor tab: $url")
                        openInNewEditorTab(url)
                        return true // cancel navigation in current tab
                    }
                    return false
                }
            },
            browser.cefBrowser
        )
    }

    private fun isHttpUrl(url: String): Boolean = url.startsWith("http://") || url.startsWith("https://")

    private fun openInNewEditorTab(url: String) {
        ApplicationManager.getApplication().invokeLater {
            try {
                val name = generateTabName(url)
                val virtualFile = LightVirtualFile(name, ShimoFileType.INSTANCE, "")
                virtualFile.putUserData(URL_KEY, url)
                FileEditorManager.getInstance(project).openFile(virtualFile, true)
            } catch (t: Throwable) {
                logger.warn("Failed to open new editor tab for URL: $url", t)
            }
        }
    }

    private fun generateTabName(url: String): String {
        return try {
            val uri = java.net.URI(url)
            val path = uri.path ?: ""
            val last = path.substringAfterLast('/', "")
            val base = if (last.isNotBlank()) last else uri.host ?: url
            if (base.isBlank()) url else base
        } catch (e: Exception) {
            url
        }
    }

    /**
     * Returns the browser component to be displayed in the UI.
     */
    fun getComponent(): JComponent {
        return browser.component
    }

    private fun installDownloadHandler(client: JBCefClient) {
        client.addDownloadHandler(object : CefDownloadHandler {
            override fun onBeforeDownload(
                browser: CefBrowser?,
                downloadItem: CefDownloadItem?,
                suggestedName: String?,
                callback: CefBeforeDownloadCallback?
            ) {
                try {
                    val state = ShimoSettingsState.getInstance()
                    val baseDir = state.downloadDirectory?.takeIf { it.isNotBlank() }
                    val fileName = suggestedName?.takeUnless { it.isBlank() }
                        ?: "download_${System.currentTimeMillis()}"

                    if (baseDir.isNullOrBlank()) {
                        // No configured path; let JCEF show its default dialog
                        callback?.Continue(fileName, true)
                        return
                    }

                    val safeDir = File(baseDir)
                    if (!safeDir.exists()) safeDir.mkdirs()

                    val target = uniqueFile(File(safeDir, sanitizeFileName(fileName)))
                    // Continue download without showing dialog
                    callback?.Continue(target.absolutePath, false)
                } catch (t: Throwable) {
                    logger.warn("onBeforeDownload failed; falling back to default dialog", t)
                    callback?.Continue(suggestedName ?: "download", true)
                }
            }

            override fun onDownloadUpdated(
                browser: CefBrowser?,
                downloadItem: CefDownloadItem?,
                callback: CefDownloadItemCallback?
            ) {
                if (downloadItem != null && downloadItem.isCanceled) {
                    logger.info("Download canceled: ${downloadItem.suggestedFileName}")
                }
                if (downloadItem != null && downloadItem.isComplete) {
                    logger.info("Download completed: ${downloadItem.fullPath}")
                }
            }
        }, browser.cefBrowser)
    }

    private fun sanitizeFileName(name: String): String {
        val cleaned = name.replace(Regex("[\\/:*?\"<>|]"), "_")
        return cleaned.ifBlank { "download_${System.currentTimeMillis()}" }
    }

    private fun uniqueFile(base: File): File {
        if (!base.exists()) return base
        val name = base.nameWithoutExtension
        val ext = base.extension
        var idx = 1
        while (true) {
            val candidate = File(base.parentFile, if (ext.isNotEmpty()) "$name ($idx).$ext" else "$name ($idx)")
            if (!candidate.exists()) return candidate
            idx++
        }
    }

    /**
     * Disposes of the browser resources when the panel is no longer needed.
     */
    override fun dispose() {
        Disposer.dispose(browser)
    }
}
