package i.s.p.j.intellijshimopluginjcef.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import i.s.p.j.intellijshimopluginjcef.ui.ShimoBrowserPanel
import java.beans.PropertyChangeListener
import javax.swing.JComponent

/**
 * A custom file editor that displays Shimo documents in a JCEF browser.
 * This editor is used to show Shimo in an editor tab rather than a tool window.
 */
class ShimoFileEditor(private val project: Project, private val file: VirtualFile) : UserDataHolderBase(), FileEditor {
    private val browserPanel: ShimoBrowserPanel
    
    init {
        // Get the URL from the virtual file's user data, or use the default URL
        val url = file.getUserData(ShimoBrowserPanel.URL_KEY) ?: ShimoBrowserPanel.DEFAULT_URL
        browserPanel = ShimoBrowserPanel(project, url)
    }
    
    override fun getComponent(): JComponent {
        return browserPanel.getComponent()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return component
    }

    override fun getName(): String {
        return "Shimo"
    }

    override fun setState(state: FileEditorState) {
        // No state to restore
    }

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        // No properties to track
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        // No properties to track
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun dispose() {
        Disposer.dispose(browserPanel)
    }
}