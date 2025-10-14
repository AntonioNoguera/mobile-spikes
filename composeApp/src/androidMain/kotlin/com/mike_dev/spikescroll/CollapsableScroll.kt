package com.mike_dev.spikescroll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlin.math.abs

@Composable
fun NestedScrollDemoScreen(
    onBack: () -> Unit = {}
) {
    var bottomSheetOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val bottomSheetMinHeight = with(density) { 200.dp.toPx() } // Altura inicial
        val bottomSheetMaxHeight = screenHeightPx * 0.80f // 80% de la pantalla

        // Estado del scroll interno del bottom sheet
        val innerScrollState = rememberScrollState()

        // Control del nested scroll
        val nestedScrollConnection = remember {
            object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                    val delta = available.y

                    // Calcular nueva posición del bottom sheet
                    val newOffset = (bottomSheetOffset + delta).coerceIn(
                        -(bottomSheetMaxHeight - bottomSheetMinHeight),
                        0f
                    )

                    // Si el bottom sheet no está completamente expandido
                    if (bottomSheetOffset > -(bottomSheetMaxHeight - bottomSheetMinHeight)) {
                        val consumed = newOffset - bottomSheetOffset
                        bottomSheetOffset = newOffset
                        return Offset(0f, consumed)
                    }

                    // Si el bottom sheet está expandido y scrolleamos hacia arriba
                    if (delta < 0 && innerScrollState.value == 0) {
                        val consumed = newOffset - bottomSheetOffset
                        bottomSheetOffset = newOffset
                        return Offset(0f, consumed)
                    }

                    return Offset.Zero
                }

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ): Offset {
                    val delta = available.y

                    // Si scrolleamos hacia abajo y el scroll interno está en el tope
                    if (delta > 0 && innerScrollState.value == 0) {
                        val newOffset = (bottomSheetOffset + delta).coerceIn(
                            -(bottomSheetMaxHeight - bottomSheetMinHeight),
                            0f
                        )
                        val consumed = newOffset - bottomSheetOffset
                        bottomSheetOffset = newOffset
                        return Offset(0f, consumed)
                    }

                    return Offset.Zero
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection)
        ) {
            // Imagen de fondo
            AsyncImage(
                model = "https://picsum.photos/800/1200",
                contentDescription = "Background",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Overlay oscuro
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
            )

            // Botón volver arriba izquierda
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Información de debug en la esquina superior derecha
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "Expansión: ${(abs(bottomSheetOffset) / (bottomSheetMaxHeight - bottomSheetMinHeight) * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "Scroll interno: ${innerScrollState.value}px",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // Bottom Sheet
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) {
                        (bottomSheetMinHeight - bottomSheetOffset).toDp()
                    })
                    .align(Alignment.BottomCenter),
                shape = MaterialTheme.shapes.extraLarge.copy(
                    bottomStart = androidx.compose.foundation.shape.CornerSize(0.dp),
                    bottomEnd = androidx.compose.foundation.shape.CornerSize(0.dp)
                ),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 14.dp)
                ) {

                    // Contenido scrollable del bottom sheet
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(innerScrollState)
                            .padding(horizontal = 24.dp)
                    ) {
                        Text(
                            text = "Demo: Nested Scroll",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "Desliza hacia arriba para expandir el bottom sheet hasta el 70%. Luego podrás scrollear el contenido interno.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Indicador visual del estado
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val expansionPercent = (abs(bottomSheetOffset) / (bottomSheetMaxHeight - bottomSheetMinHeight) * 100).toInt()

                                Text(
                                    text = if (expansionPercent < 100) {
                                        "🔼 Expandiendo: $expansionPercent%"
                                    } else {
                                        "✅ Completamente expandido - Scroll habilitado"
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                LinearProgressIndicator(
                                    progress = {
                                        abs(bottomSheetOffset) / (bottomSheetMaxHeight - bottomSheetMinHeight)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }

                        // Contenido largo para probar el scroll
                        repeat(30) { index ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Elemento ${index + 1}",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Este contenido solo será scrollable cuando el bottom sheet alcance el 70% de expansión. " +
                                                "Primero el sheet sube, luego puedes scrollear aquí dentro.",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Volver")
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Barra de progreso superior
            LinearProgressIndicator(
                progress = {
                    abs(bottomSheetOffset) / (bottomSheetMaxHeight - bottomSheetMinHeight)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}