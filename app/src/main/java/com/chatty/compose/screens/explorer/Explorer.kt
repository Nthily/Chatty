package com.chatty.compose.screens.explorer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.chatty.compose.R
import com.chatty.compose.ui.components.*
import com.chatty.compose.ui.theme.chattyColors
import com.chatty.compose.ui.utils.LocalNavController
import com.chatty.compose.ui.utils.hideIME
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun Explorer() {

    val lazyState = rememberLazyListState()
    val navController = LocalNavController.current
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    val firstItemSize by remember {
        derivedStateOf {
            if (lazyState.layoutInfo.visibleItemsInfo.isNotEmpty())
                lazyState.layoutInfo.visibleItemsInfo[0].size
            else null
        }
    }

    val topBarAlpha by remember {
        derivedStateOf {
            if (lazyState.firstVisibleItemIndex == 0) {
                firstItemSize?.let {
                    lazyState.firstVisibleItemScrollOffset.dp / it.dp
                } ?: 0f
            } else 1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.chattyColors.backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = lazyState,
            contentPadding = WindowInsets.statusBars.asPaddingValues()
        ) {
            item {
                CenterRow(
                    modifier = Modifier
                        .padding(14.dp)
                        .alpha(1 - topBarAlpha)
                ) {
                    Text(
                        text = "???????????????",
                        style = MaterialTheme.typography.h4,
                        color = MaterialTheme.chattyColors.textColor
                    )
                    Spacer(Modifier.weight(1f))
                    CircleShapeImage(size = 48.dp, painter = painterResource(id = R.drawable.ava4))
                }
                Divider(modifier = Modifier.fillMaxWidth())
            }
            items(20) {
                SocialItem(
                    R.drawable.ava5,
                    name = "???????????????",
                    focusRequester = focusRequester
                )
            }
        }
        ExplorerTopBar(topBarAlpha)
        ExplorerFab(
            boxScope = this,
            targetState = topBarAlpha,
            editAction = {
                scope.launch { navController.navigate(AppScreen.createPost) }
            }
        ) {
            scope.launch { lazyState.scrollToItem(0) }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SocialItem(
    avaRes: Int,
    name: String,
    time: String = "22 ????????????",
    content: String = "?????????????????????????????????????????????????????????????????? ?????????????????????????????????????????????????????? ???????????????????????????????????????????????? ????????????????????????????????? ????????????????????????????????? ?????????????????????????????? ???????????????????????????????????????????????? ???????????????????????? ????????????????????????????????????????????????????????????????????????",
    focusRequester: FocusRequester
) {

    var text by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var isAlreadyExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .clickable { }
            .padding(16.dp)
    ) {
        CenterRow {
            CircleShapeImage(40.dp, painterResource(avaRes))
            WidthSpacer(value = 4.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.chattyColors.textColor
                )
                HeightSpacer(value = 2.dp)
                Text(
                    text = time,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.alpha(0.5f),
                    color = MaterialTheme.chattyColors.textColor
                )
            }
        }
        HeightSpacer(value = 4.dp)
        Text(
            text = content,
            color = MaterialTheme.chattyColors.textColor
        )
        CenterRow(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Icon(Icons.Rounded.Favorite, null, tint = MaterialTheme.chattyColors.iconColor.copy(0.5f))
            IconButton(
                onClick = {
                    isExpanded = !isExpanded
                }
            ) {
                Icon(
                    Icons.Rounded.ChatBubble,
                    null,
                    tint = MaterialTheme.chattyColors.iconColor.copy(0.5f),
                )
            }
            Icon(Icons.Rounded.Share, null, tint = MaterialTheme.chattyColors.iconColor.copy(0.5f))
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = scaleIn(tween(400)),
            exit = scaleOut(tween(200))
        ) {
            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (isAlreadyExpanded && !it.hasFocus) {
                            isExpanded = false
                            isAlreadyExpanded = false
                        }
                    },
                decorationBox = {
                    Surface(
                        elevation = 6.dp,
                        shape = CircleShape
                    ) {
                        CenterRow(Modifier.padding(8.dp)) {
                            CircleShapeImage(40.dp, painterResource(R.drawable.ava4))
                            WidthSpacer(value = 5.dp)
                            Box(Modifier.weight(1f)) {
                                if (text.isEmpty()) {
                                    Text(
                                        text = "???????????????????????????~",
                                        color = MaterialTheme.chattyColors.textColor.copy(0.5f)
                                    )
                                }
                                it()
                            }
                            if (text.isNotEmpty()) {
                                IconButton(
                                    onClick = {
                                        isExpanded = false
                                        isAlreadyExpanded = false
                                        context.hideIME()
                                    }
                                ) {
                                    Icon(Icons.Rounded.Send, null, tint = MaterialTheme.chattyColors.iconColor)
                                }
                            }
                        }
                    }
                },
                textStyle = MaterialTheme.typography.subtitle2,
                maxLines = 3
            )
        }
    }
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            delay(700)
            focusRequester.requestFocus()
            isAlreadyExpanded = true
        }
    }
    
    BackHandler(isAlreadyExpanded) {
        isExpanded = false
        isAlreadyExpanded = false
    }

}
