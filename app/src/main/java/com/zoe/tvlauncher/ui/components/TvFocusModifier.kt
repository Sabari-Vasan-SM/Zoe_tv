package com.zoe.tvlauncher.ui.components

import android.view.KeyEvent as AndroidKeyEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

/**
 * TV D-Pad Focus & Animation Modifier
 * Provides smooth 10-foot TV experience with scale, glow border, and TV remote key handling.
 */
@Composable
fun Modifier.tvFocusable(
    focusRequester: FocusRequester? = null,
    scaleFactor: Float = 1.07f,
    shape: Shape = RoundedCornerShape(16.dp),
    focusedBorderColor: Color = Color(0xFF00E5FF),
    unfocusedBorderColor: Color = Color(0x1AFFFFFF),
    borderWidth: Dp = 2.dp,
    elevation: Dp = 8.dp,
    onFocused: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleFactor else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tvFocusScale"
    )

    val currentBorderColor = if (isFocused) focusedBorderColor else unfocusedBorderColor
    val currentBorderWidth = if (isFocused) borderWidth else 1.dp

    var mod = this
        .scale(scale)
        .zIndex(if (isFocused) 10f else 1f)

    if (focusRequester != null) {
        mod = mod.focusRequester(focusRequester)
    }

    return mod
        .onFocusChanged { state ->
            isFocused = state.isFocused
            if (state.isFocused) {
                onFocused?.invoke()
            }
        }
        .focusable()
        .onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyUp) {
                val nativeEvent = keyEvent.nativeKeyEvent
                when (nativeEvent.keyCode) {
                    AndroidKeyEvent.KEYCODE_DPAD_CENTER,
                    AndroidKeyEvent.KEYCODE_ENTER,
                    AndroidKeyEvent.KEYCODE_NUMPAD_ENTER -> {
                        onClick()
                        true
                    }
                    AndroidKeyEvent.KEYCODE_MENU -> {
                        if (onLongClick != null) {
                            onLongClick()
                            true
                        } else false
                    }
                    else -> false
                }
            } else if (keyEvent.type == KeyEventType.KeyDown) {
                val nativeEvent = keyEvent.nativeKeyEvent
                // Detect long press on DPAD center
                if (nativeEvent.keyCode == AndroidKeyEvent.KEYCODE_DPAD_CENTER &&
                    nativeEvent.isLongPress &&
                    onLongClick != null
                ) {
                    onLongClick()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }
        .clip(shape)
        .border(
            BorderStroke(currentBorderWidth, currentBorderColor),
            shape = shape
        )
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        }
}
