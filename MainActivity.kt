package com.example.cameracontrols

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cameracontrols.ui.theme.CameraControlsTheme
import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import coil.compose.AsyncImage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CameraControlsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PermissionSelection()
                }
            }
        }
    }
}

fun checkAndRequestCameraPermissions(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, permission)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
    } else {
        // Request a permission
        launcher.launch(permission)
    }
}

fun checkAndRequestFilePermissions(
    context: Context,
    permission: String,
    launcher: ManagedActivityResultLauncher<String, Boolean>
) {
    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
        // Open camera because permission is already granted
    } else {
        // Request a permission

        launcher.launch(permission)
    }
}

@Composable
fun PermissionSelection(
    modifier: Modifier = Modifier,
) {
    // 1
    var hasImage by remember {
        mutableStateOf(false)
    }
    // 2
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val context = LocalContext.current

    val cam_permission = Manifest.permission.CAMERA
    val cam_launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "Camera Access was Approved", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Camera Access was Denied", Toast.LENGTH_LONG).show()
        }
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            // 3
            hasImage = uri != null
            imageUri = uri
        }
    )

    val file_permission = Manifest.permission.READ_MEDIA_IMAGES
    val file_launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(context, "File Access was Approved", Toast.LENGTH_LONG).show()
            imagePicker.launch("image/*")
        } else {
            Toast.makeText(context, "File Access was Denied", Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = modifier,
    ) {
        // 4
        if (hasImage && imageUri != null) {
            // 5
            AsyncImage(
                model = imageUri,
                modifier = Modifier.fillMaxWidth(),
                contentDescription = "Selected image",
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    checkAndRequestFilePermissions(context, file_permission, file_launcher)
                    // TODO
                },
            ) {
                Text(
                    text = "File Permissions"
                )
            }
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    imagePicker.launch("image/*")
                    // TODO
                },
            ) {
                Text(
                    text = "Open Files"
                )
            }
            Button(
                modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    checkAndRequestCameraPermissions(context, cam_permission, cam_launcher)
                    // TODO
                },
            ) {
                Text(
                    text = "Camera Permissions"
                )
            }
        }
    }
}
