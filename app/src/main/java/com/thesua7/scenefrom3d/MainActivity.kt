package com.thesua7.scenefrom3d

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thesua7.scenefrom3d.ui.theme.Scenefrom3dTheme
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scenefrom3dTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShowChair3DModel(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShowChair3DModel(modifier: Modifier) {
    // Initialize the Filament engine and related objects
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)

    // Load the 3D chair model as a ModelNode
    val chairNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(
            assetFileLocation = "models/scene.gltf" // Ensure correct file path in assets folder
        ),
        scaleToUnits = 1.0f // Adjust scale if needed
    )

    Scene(
        modifier = Modifier.fillMaxSize(),
        engine = engine,
        view = view,
        renderer = renderer,
        scene = scene,
        mainLightNode = rememberMainLightNode(engine) {
            intensity = 100_000.0f // Set the intensity of the main light source
        },
        cameraNode = rememberCameraNode(engine) {
            position = Position(z = 3.0f) // Position the camera to view the model
        },
        childNodes = listOf(chairNode), // Add the chairNode to the scene
        onTouchEvent = { event: MotionEvent, hitResult ->
            hitResult?.let {
                println("Tapped at position: ${it.worldPosition}")
            }
            false // Return false to not consume the event
        }
    )
}
