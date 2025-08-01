package i.s.p.j.intellijshimopluginjcef.toolwindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import i.s.p.j.intellijshimopluginjcef.ui.ShimoBrowserPanel

/**
 * Factory for creating the Shimo tool window.
 * This tool window displays a JCEF browser that loads the Shimo desktop web application.
 */
class ShimoToolWindowFactory : ToolWindowFactory {
    private val logger = Logger.getInstance(ShimoToolWindowFactory::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        logger.info("Creating Shimo tool window content")
        
        // Create browser panel
        val browserPanel = ShimoBrowserPanel()
        
        // Create content for the tool window
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(
            browserPanel.getComponent(),
            null, // No display name for the content
            false // Not pinnable
        )
        
        // Dispose the browser panel when the content is disposed
        Disposer.register(content, browserPanel)
        
        // Add content to the tool window
        toolWindow.contentManager.addContent(content)
    }
}