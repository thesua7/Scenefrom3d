package com.thesua7.scenefrom3d

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.thesua7.scenefrom3d.ui.theme.Scenefrom3dTheme
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
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
                    Show3DModels(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Show3DModels(modifier: Modifier) {
    // Initialize the Filament engine and related objects
    val engine = rememberEngine()
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val scene = rememberScene(engine)
    val modelLoader = rememberModelLoader(engine)

    // State for rotation
    var rotationValue by remember { mutableStateOf(0f) } // Rotation in degrees

    // Create ModelNodes for chair and base
    val chairNode = remember {
        try {
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = "models/scene.gltf" // Ensure correct file path
                ), scaleToUnits = 1.0f
            )
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
            null // Handle the failure gracefully
        }
    }

    val baseNode = remember {
        try {
            ModelNode(
                modelInstance = modelLoader.createModelInstance(
                    assetFileLocation = "base/result.gltf"
                ),
                scaleToUnits = 1.0f
            )
        } catch (e: Exception) {
            e.printStackTrace() // Log the exception
            null // Handle the failure gracefully
        }
    }

    // Apply the base rotation to lie flat



    // Update rotation for both nodes
    chairNode?.rotation = Rotation(y = rotationValue,x=30f)
    baseNode?.rotation = Rotation(x = 120f, y = rotationValue)

    Box(modifier = Modifier.fillMaxSize()) {
        if (chairNode != null && baseNode != null) {
            Scene(
                modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                    // Consume all touch inputs to prevent interaction
                },
                engine = engine,
                view = view,
                renderer = renderer,
                scene = scene,
                mainLightNode = rememberMainLightNode(engine) {
                    intensity = 100_000.0f
                },
                cameraNode = rememberCameraNode(engine) {
                    Log.d("CameraNode",position.toString())
                    position = Position(z = 3.0f)
                },
                childNodes = listOf(chairNode, baseNode),
                // Add both nodes to the scene
            ){
                this.setOnTouchListener { _, _ -> true } // Prevent touch interactions

            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Rotate Models", style = MaterialTheme.typography.bodyMedium)
            Slider(
                value = rotationValue,
                onValueChange = {
                    rotationValue = it
                    chairNode?.rotation = Rotation(y = it)
                    baseNode?.rotation = Rotation(x = 90f, y = it) // Keep the base lying flat

                    // Log updated rotations
                    Log.d("RotationLogger", "Updated Chair Rotation: ${chairNode?.rotation}")
                    Log.d("RotationLogger", "Updated Base Rotation: ${baseNode?.rotation}")
                },
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth(0.8f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Result", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
