package i.s.p.j.intellijshimopluginjcef.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindowManager

/**
 * Action that opens the Shimo tool window.
 * This action is triggered when the user selects "打开石墨文档" from the "石墨" menu.
 */
class OpenShimoAction : AnAction() {
    private val logger = Logger.getInstance(OpenShimoAction::class.java)
    private val TOOL_WINDOW_ID = "Shimo"

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        logger.info("Opening Shimo tool window")

        // Get the tool window manager for the current project
        val toolWindowManager = ToolWindowManager.getInstance(project)

        // Get the Shimo tool window
        val toolWindow = toolWindowManager.getToolWindow(TOOL_WINDOW_ID)

        if (toolWindow != null) {
            // Show and activate the tool window
            toolWindow.show()
        } else {
            logger.error("Failed to find Shimo tool window with ID: $TOOL_WINDOW_ID")
        }
    }

    override fun update(e: AnActionEvent) {
        // Enable the action only when a project is open
        e.presentation.isEnabled = e.project != null
    }
}