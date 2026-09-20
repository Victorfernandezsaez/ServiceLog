package com.example.servicelog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Composable
    fun ExtendedExample(onClick: () -> Unit) {
        ExtendedFloatingActionButton(
            onClick = { onClick() },
            icon = { Icon(Icons.Filled.Edit, "Extended floating action button.") },
            text = { Text(text = "Extended FAB") },
        )
    }
}


