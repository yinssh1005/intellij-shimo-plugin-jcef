package i.s.p.j.intellijshimopluginjcef.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.testFramework.LightVirtualFile
import i.s.p.j.intellijshimopluginjcef.file.ShimoFileType
import i.s.p.j.intellijshimopluginjcef.ui.ShimoBrowserPanel

/**
 * Action that opens Shimo in an editor tab.
 * This action is triggered when the user selects "打开石墨文档" from the "石墨" menu.
 */
class OpenShimoAction : AnAction() {
    private val logger = Logger.getInstance(OpenShimoAction::class.java)
    private val EDITOR_TAB_NAME = "Shimo"

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        logger.info("Opening Shimo in editor tab")

        // Create a light virtual file with our custom file type
        val virtualFile = LightVirtualFile(EDITOR_TAB_NAME, ShimoFileType.INSTANCE, "")
        
        // Store the URL as user data on the virtual file
        virtualFile.putUserData(ShimoBrowserPanel.URL_KEY, ShimoBrowserPanel.DEFAULT_URL)
        
        // Open the virtual file in the editor
        FileEditorManager.getInstance(project).openFile(virtualFile, true)
    }

    override fun update(e: AnActionEvent) {
        // Enable the action only when a project is open
        e.presentation.isEnabled = e.project != null
    }
}