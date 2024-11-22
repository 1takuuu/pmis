package com.example.helloapp
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.helloapp.ui.theme.HelloAppTheme
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Main()
        }
}
}
class ComposeFileProvider: FileProvider(R.xml.file_paths){
    companion object{
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}
@Composable
fun Main() {
    val navController = rememberNavController()
    Column(Modifier.padding(8.dp)) {
        NavHost(navController, startDestination = NavRoutes.Home.route, modifier
        = Modifier.weight(1f)) {
            composable(NavRoutes.Home.route) { Home() }
            composable(NavRoutes.List.route) { List() }
            composable(NavRoutes.Drawing.route) { BabyScreen() }
            composable(NavRoutes.Camera.route) { FakeCam() }
        }
        BottomNavigationBar(navController = navController)
    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {saveState = true}
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Filled.Home,
            route = "home"
        ),
        BarItem(
            title = "List",
            image = Icons.AutoMirrored.Filled.List,
            route = "list"
        ),
        BarItem(
            title = "Drawing",
            image = Icons.Filled.Info,
            route = "drawing"
        ),
        BarItem(
            title = "Camera",
            image = Icons.Filled.Face,
            route = "camera"
        )
    )
}
data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

@Composable
fun Home(modifier: Modifier = Modifier){
    var text by rememberSaveable { mutableStateOf("") }
    val textValue = stringResource(id = R.string.textValue)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFD7EC9C)),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = text,
            onValueChange = { newText -> text = newText },
            modifier = modifier.padding(64.dp),
            enabled = false

        )
        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(
                onClick =
                {
                    text = textValue

                }
            ) {
                Text(text = "Вывести имя")
            }
            Button(
                onClick = {
                    text = ""
                }
            )
            {
                Text(text = "X")
            }
        }
    }
}
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun List(modifier: Modifier = Modifier){
    Column (modifier = modifier.fillMaxSize().background(Color(0xFFC3C8F1)),
        horizontalAlignment = Alignment.CenterHorizontally) {
        val people = listOf(
            Phone("China", "BingChilling"), Phone("Vietnam", "BingChilling"),
            Phone("Russia", "R1"), Phone("Turkey", "SerBolat"),
            Phone("Botswana", "UgaBuga"), Phone("Belarus", "MTS"),
            Phone("Ukraine", "U1"), Phone("Belarus", "A1"),
            Phone("Thailand", "TP"), Phone("Poland", "Kurwa")
        )
        val groups = people.groupBy { it.provider }
        LazyColumn(
            contentPadding = PaddingValues(5.dp)
        ) {
            groups.forEach { (company, employees) ->
                stickyHeader {
                    Text(
                        text = company,
                        fontSize = 28.sp,
                        color = Color.White,
                        modifier =
                        Modifier.background(Color.Gray).padding(5.dp).fillMaxWidth()
                    )
                }
                items(employees) { employee ->
                    Text(employee.country, Modifier.padding(5.dp), fontSize = 28.sp)
                }
            }
        }
    }
}
data class Phone(val country:String, val provider: String)

@Composable
fun BabyScreen(modifier: Modifier = Modifier) {
    var rotation by remember { mutableStateOf(0f) }
    val animatedRotation = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(rotation) {
        animatedRotation.animateTo(targetValue = rotation, animationSpec = tween(durationMillis = 2000, easing = LinearEasing))
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
        Column(
            modifier = modifier.fillMaxSize().background(Color(0xFF9A60EC)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.omg),
                contentScale = ContentScale.Crop,
                contentDescription = "ass",
                modifier = Modifier.size(240.dp).clip(RoundedCornerShape(50)).rotate(animatedRotation.value)
            )
            Button(
                onClick = {
                    rotation += 360f
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Включить стиралку!")
            }
        }
    } else {
        Row(
            modifier = modifier.fillMaxSize().background(Color(0xFF9A60EC)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.omg),
                contentScale = ContentScale.Crop,
                contentDescription = "ass",
                modifier = Modifier.size(240.dp).clip(RoundedCornerShape(50)).rotate(animatedRotation.value)
            )
            Button(
                onClick = {
                    rotation += 360f
                },
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text("Включить стиралку!")
            }
        }
    }
}


@Composable
fun FakeCam() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember { mutableStateOf(false) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            hasImage = success
            if (success) {
                currentUri = imageUri
            }
        }
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Permission granted", Toast.LENGTH_SHORT).show()
                currentUri?.let { cameraLauncher.launch(it) }

            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            hasImage = uri != null
            imageUri = uri
        }
    )
    Box(modifier = Modifier) {
        if (hasImage && imageUri != null) {

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(400.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(10)),
            )
        }
        Column(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    imagePicker.launch("image/*")
                })
            { Text(text = "Выбрать изображение") }
            Button(modifier = Modifier.padding(top = 16.dp),
                onClick = {
                    currentUri = ComposeFileProvider.getImageUri(context)
                    if (currentUri == null) {
                        Toast.makeText(context, "Ошибка при получении URI", Toast.LENGTH_SHORT).show()
                    } else {
                        cameraLauncher.launch(currentUri!!)
                    }
                    if (currentUri != null) {
                        val permissionCheckResult = ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.CAMERA
                        )
                        if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                            cameraLauncher.launch(currentUri!!)
                        } else {
                            permissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    } else {
                        Toast.makeText(context, "Ошибка при получении URI", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            ) {
                Text(text = "Сделать снимок")
            }
        }

    }

}

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object List : NavRoutes("list")
    object Drawing : NavRoutes("drawing")
    object Camera: NavRoutes ("camera")
}

@Preview(showSystemUi = true)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HelloAppTheme {
        Home()
    }
}
