package i.s.p.j.intellijshimopluginjcef.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.FormBuilder
import javax.swing.JComponent
import javax.swing.JPanel

/**
 * Settings configurable for the plugin, shown under Tools -> 石墨 in Settings.
 */
class ShimoSettingsConfigurable : Configurable {
    private var panel: JPanel? = null
    private lateinit var downloadDirField: TextFieldWithBrowseButton

    override fun getDisplayName(): String = "石墨"

    override fun createComponent(): JComponent {
        if (panel == null) {
            downloadDirField = TextFieldWithBrowseButton()
            downloadDirField.addBrowseFolderListener(
                "选择下载目录",
                "选择用于保存石墨文件的本地目录",
                null,
                FileChooserDescriptorFactory.createSingleFolderDescriptor()
            )

            val form = FormBuilder.createFormBuilder()
                .addLabeledComponent(JBLabel("文件保存路径:"), downloadDirField, 1, false)
                .addComponentFillVertically(JPanel(), 0)
                .panel

            panel = form
            reset()
        }
        return panel as JComponent
    }

    override fun isModified(): Boolean {
        val state = ShimoSettingsState.getInstance()
        val current = state.downloadDirectory ?: ""
        return current != downloadDirField.text.trim()
    }

    override fun apply() {
        val state = ShimoSettingsState.getInstance()
        val path = downloadDirField.text.trim().ifBlank { null }
        state.downloadDirectory = path
    }

    override fun reset() {
        val state = ShimoSettingsState.getInstance()
        downloadDirField.text = state.downloadDirectory ?: ""
    }

    override fun disposeUIResources() {
        panel = null
    }
}