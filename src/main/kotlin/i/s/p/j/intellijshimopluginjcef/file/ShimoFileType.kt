package i.s.p.j.intellijshimopluginjcef.file

import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.Icon

/**
 * Custom file type for Shimo documents.
 * This is used to identify virtual files that should be opened with the ShimoFileEditor.
 */
class ShimoFileType : FileType {
    companion object {
        val INSTANCE = ShimoFileType()
        const val NAME = "shimo"
    }

    override fun getName(): String = NAME

    override fun getDescription(): String = "Shimo document"

    override fun getDefaultExtension(): String = "shimo"

    override fun getIcon(): Icon? = IconLoader.getIcon("/META-INF/pluginIcon.svg", ShimoFileType::class.java)

    override fun isBinary(): Boolean = false

    override fun isReadOnly(): Boolean = false

    override fun getCharset(file: VirtualFile, content: ByteArray): String? = null
}