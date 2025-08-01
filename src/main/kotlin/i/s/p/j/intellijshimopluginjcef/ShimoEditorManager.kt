package i.s.p.j.intellijshimopluginjcef

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.registry.Registry
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * Application component that manages Shimo editor configuration.
 * This component is responsible for setting up the necessary VM options
 * when the plugin is enabled.
 */
@Service
class ShimoEditorManager : ApplicationComponent {
    private val logger = Logger.getInstance(ShimoEditorManager::class.java)
    private val VM_OPTION = "-Dide.browser.jcef.out-of-process.enabled=false"
    private val OPTION_ADDED_KEY = "shimo.plugin.vm.option.added"

    override fun initComponent() {
        logger.info("Initializing ShimoEditorManager")
        addVMOption()
    }

    override fun disposeComponent() {
        // No cleanup needed
    }

    override fun getComponentName(): String {
        return "ShimoEditorManager"
    }

    /**
     * Adds the required VM option to disable JCEF out-of-process mode.
     * This is needed for proper functioning of the Shimo editor.
     */
    private fun addVMOption() {
//        if (Registry.`is`("ide.browser.jcef.out-of-process.enabled")) {
//            Registry.get("ide.browser.jcef.out-of-process.enabled").setValue(false)
//        }
        val properties = PropertiesComponent.getInstance()
        
        // Check if we've already added the VM option
        if (properties.getBoolean(OPTION_ADDED_KEY, false)) {
            logger.info("VM option already added, skipping")
            return
        }
        
        try {
            // Get the path to the custom VM options file
            val configPath = getVMOptionsFilePath()
            if (configPath == null) {
                logger.warn("Could not determine VM options file path")
                return
            }
            
            val optionsFile = File(configPath)
            
            // Create the file if it doesn't exist
            if (!optionsFile.exists()) {
                optionsFile.createNewFile()
            }
            
            // Read the current content
            val content = Files.readAllLines(Paths.get(configPath))
            
            // Check if the option is already there
            if (content.any { it.trim() == VM_OPTION }) {
                logger.info("VM option already exists in file: $configPath")
                properties.setValue(OPTION_ADDED_KEY, true)
                return
            }
            
            // Add the option
            Files.write(
                Paths.get(configPath),
                listOf(VM_OPTION),
                StandardOpenOption.APPEND
            )
            
            logger.info("Added VM option to: $configPath")
            properties.setValue(OPTION_ADDED_KEY, true)
            
        } catch (e: IOException) {
            logger.error("Failed to add VM option", e)
        }
    }
    
    /**
     * Gets the path to the custom VM options file.
     */
    private fun getVMOptionsFilePath(): String? {
        val configDir = System.getProperty("idea.config.path")
        if (configDir != null) {
            return "$configDir${File.separator}idea.vmoptions"
        }
        
        // Try to find the file in the user home directory
        val userHome = System.getProperty("user.home")
        if (userHome != null) {
            val jetbrainsConfigDir = "$userHome${File.separator}.config${File.separator}JetBrains"
            val configDirFile = File(jetbrainsConfigDir)
            
            if (configDirFile.exists() && configDirFile.isDirectory) {
                // Look for the most recent config directory
                val ideaConfigDirs = configDirFile.listFiles { file -> 
                    file.isDirectory && file.name.startsWith("Idea") 
                }
                
                if (ideaConfigDirs != null && ideaConfigDirs.isNotEmpty()) {
                    // Sort by last modified time to get the most recent
                    val mostRecentDir = ideaConfigDirs.maxByOrNull { it.lastModified() }
                    if (mostRecentDir != null) {
                        return "${mostRecentDir.absolutePath}${File.separator}idea.vmoptions"
                    }
                }
            }
        }
        
        return null
    }
    
    companion object {
        fun getInstance(): ShimoEditorManager {
            return ApplicationManager.getApplication().getService(ShimoEditorManager::class.java)
        }
    }
}