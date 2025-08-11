package i.s.p.j.intellijshimopluginjcef.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.annotations.Attribute

@State(name = "ShimoSettingsState", storages = [Storage("shimo_settings.xml")])
class ShimoSettingsState : PersistentStateComponent<ShimoSettingsState.State> {
    data class State(
        @Attribute("downloadDirectory") var downloadDirectory: String? = null
    )

    private var myState: State = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        this.myState = state
    }

    var downloadDirectory: String?
        get() = myState.downloadDirectory
        set(value) { myState.downloadDirectory = value }

    companion object {
        @JvmStatic
        fun getInstance(): ShimoSettingsState = com.intellij.openapi.application.ApplicationManager.getApplication()
            .getService(ShimoSettingsState::class.java)
    }
}