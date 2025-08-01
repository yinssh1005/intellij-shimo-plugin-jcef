package i.s.p.j.intellijshimopluginjcef.ui

import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.Key
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefClient
import javax.swing.JComponent

/**
 * Panel that hosts a JCEF browser for displaying Shimo documents.
 */
class ShimoBrowserPanel(private val url: String = "https://shimo.im/desktop") : Disposable {
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
        browser.loadURL(url)
    }

    /**
     * Returns the browser component to be displayed in the UI.
     */
    fun getComponent(): JComponent {
        return browser.component
    }

    /**
     * Disposes of the browser resources when the panel is no longer needed.
     */
    override fun dispose() {
        Disposer.dispose(browser)
    }
}