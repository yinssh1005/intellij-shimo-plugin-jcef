package i.s.p.j.intellijshimopluginjcef.editor

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import i.s.p.j.intellijshimopluginjcef.file.ShimoFileType

/**
 * Provider for creating ShimoFileEditor instances.
 * This provider is registered in plugin.xml and is used to create editors for Shimo virtual files.
 */
class ShimoFileEditorProvider : FileEditorProvider, DumbAware {
    companion object {
        const val EDITOR_TYPE_ID = "ShimoEditor"
    }

    override fun accept(project: Project, file: VirtualFile): Boolean {
        // Accept only virtual files with our custom type
        return file.fileType == ShimoFileType.INSTANCE
    }

    override fun createEditor(project: Project, file: VirtualFile): FileEditor {
        return ShimoFileEditor(project, file)
    }

    override fun getEditorTypeId(): String {
        return EDITOR_TYPE_ID
    }

    override fun getPolicy(): FileEditorPolicy {
        // This editor should be the only one used for Shimo files
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}