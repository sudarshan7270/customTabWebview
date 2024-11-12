package com.customwebviewtab.customtabwebview;

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.customwebviewtab.customtabwebview.CustomWebViewTabActivity
import com.customwebviewtab.customtabwebview.ui.theme.CustomTabWebviewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomTabWebviewTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Log.e("Log clicke","Hellow Android clikced")
                        // Launch CustomWebViewTabActivity with the URL
                        val intent = Intent(this, CustomWebViewTabActivity::class.java)
                            intent.putExtra("urlToOpenInWebview", "https://fgnluat.fggeneral.in/PortalGroup/Home")
                            startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = "Hello $name! Click here to open WebView",
        modifier = modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CustomTabWebviewTheme {
        Greeting("Android", onClick = {})
    }
}
