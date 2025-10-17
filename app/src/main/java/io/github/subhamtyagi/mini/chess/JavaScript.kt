package io.github.subhamtyagi.mini.chess

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.subhamtyagi.mini.chess.utils.Size

val jsRemoveElements = """
        (function() {
        
        var meta = document.querySelector('meta[name=viewport]');
            if (!meta) {
                meta = document.createElement('meta');
                meta.name = 'viewport';
                meta.content = 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no';
                document.getElementsByTagName('head')[0].appendChild(meta);
            } else {
                meta.setAttribute('content', 'width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no');
            }
              
            // Remove top header
            document.querySelector('#top')?.remove();
            // Remove right panel (tools)
            //document.querySelector('.analyse__tools')?.remove();
            // Remove left sidebar
            document.querySelector('.analyse__side')?.remove();
            // Remove bottom controls
            //document.querySelector('.analyse__controls')?.remove();
            //document.querySelector('.analyse__underboard')?.remove();
            // Resize main board area
            //const board = document.querySelector('.analyse__board');
            //if (board) {
              //  board.style.transform = 'scale(0.9)';
             //   board.style.transformOrigin = 'top left';
            //}
        })();
    """.trimIndent()

// .analyse__board -main board
// .eval-gauge
//.analyse__tools -game pgn
// .analyse__side - wiki book
//.mselect - mode
@Composable
fun FloatingWindowSizeSelector(
    selectedOption: Size,
    onOptionSelected: (Size) -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Choose Floating Window Size",
            fontSize = 18.sp,
            fontWeight = FontWeight.Companion.Bold,
            modifier = Modifier.Companion.padding(bottom = 16.dp)
        )

        val options = listOf(Size.SMALL, Size.MEDIUM, Size.LARGE)

        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.Companion.CenterVertically,
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(vertical = 4.dp)
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = when (option) {
                        Size.SMALL -> "Small"
                        Size.MEDIUM -> "Medium"
                        Size.LARGE -> "Large"
                    },
                    modifier = Modifier.Companion.padding(start = 8.dp)
                )
            }
        }

        Button(
            onClick = onConfirmClick,
            modifier = Modifier.Companion.padding(top = 20.dp)
        ) {
            Text("Open Floating Window")
        }
    }
}