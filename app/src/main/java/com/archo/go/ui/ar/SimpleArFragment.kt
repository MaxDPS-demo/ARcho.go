package com.archo.go.ui.ar

import com.google.ar.sceneform.ux.ArFragment

class SimpleArFragment : ArFragment() {
    override fun getSessionConfiguration(session: com.google.ar.core.Session): com.google.ar.core.Config {
        val config = super.getSessionConfiguration(session)
        config.planeFindingMode = com.google.ar.core.Config.PlaneFindingMode.HORIZONTAL
        arSceneView.setupSession(session)
        return config
    }
}
