package com.chatty.compose.screens.contracts

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.chatty.compose.R
import com.chatty.compose.bean.UserProfileData
import com.chatty.compose.screens.chatty.mock.friends
import com.chatty.compose.ui.components.AppScreen
import com.chatty.compose.ui.components.CenterRow
import com.chatty.compose.ui.components.CircleShapeImage
import com.chatty.compose.ui.components.TopBar
import com.chatty.compose.ui.theme.chattyColors
import com.chatty.compose.ui.theme.green
import com.chatty.compose.ui.utils.*
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


data class AlphaState(
    val alpha: Char,
    var state: MutableState<Boolean>
)

fun fetchLatestFriendsList(): List<UserProfileData> {
    return friends
}

@Preview
@Composable
fun Contracts() {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var currentSelectedAlphaIndex by remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        ContractTopBar()
        var currentFriends = fetchLatestFriendsList()
        var sortedFriends = remember(currentFriends){
            currentFriends.groupBy {
                var firstChar = Pinyin.toPinyin(it.nickname.first()).first()
                if (!firstChar.isLetter()) { '#' }
                else {
                    firstChar.uppercaseChar()
                }
            }.toSortedMap { a: Char, b: Char ->
                when {
                    a == b -> 0
                    a == '#' -> 1
                    b == '#' -> -1
                    else -> a.compareTo(b)
                }
            }.apply {
                for((k, v) in entries) {
                    put(k, v.sortedWith {
                            a, b -> a.nickname.compareTo(b.nickname)
                    })
                }
            }
        }
        val preSumIndexToStateMap = remember(sortedFriends) { mutableMapOf<Int, AlphaState>() }
        val alphaCountPreSumList = remember(sortedFriends) {
            var currentSum = 0
            var index = 0
            var result = mutableListOf<Int>()
            for ((alpha, friendList) in sortedFriends) {
                preSumIndexToStateMap[index] = AlphaState(alpha, mutableStateOf(false))
                result.add(currentSum)
                currentSum += (friendList.size + 1)
                index++
            }
            result
        }
        val lazyListState = rememberLazyListState()
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = lazyListState
            ) {
                sortedFriends.forEach { it ->
                    item {
                        Text(
                            text = it.key.toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.chattyColors.backgroundColor)
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            fontWeight = FontWeight.W700,
                            color = Color(0xFF0079D3)
                        )
                    }
                    itemsIndexed(items = it.value) { index, user ->
                        FriendItem(user.avatarRes, user.nickname, user.motto) {
                            navController.navigate("${AppScreen.conversation}/${user.uid}")
                        }
                    }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd) {
                AlphaGuildBar(preSumIndexToStateMap.values) { selectIndex ->
                    scope.launch {
                        lazyListState.scrollToItem(alphaCountPreSumList[selectIndex])
                        var newestAlphaIndex = alphaCountPreSumList.searchLastElementIndex(object: Comparator<Int> {
                            override fun compare(target: Int): Boolean {
                                return target <= lazyListState.firstVisibleItemIndex
                            }
                        })
                        currentSelectedAlphaIndex = newestAlphaIndex
                        if (newestAlphaIndex == selectIndex) { // ?????????????????????
                            context.vibrate(50)
                        }
                    }
                }
            }
        }
        LaunchedEffect(lazyListState.firstVisibleItemIndex) {
            currentSelectedAlphaIndex = alphaCountPreSumList.searchLastElementIndex(object: Comparator<Int> {
                override fun compare(target: Int): Boolean {
                    return target <= lazyListState.firstVisibleItemIndex
                }
            })
        }

        LaunchedEffect(currentSelectedAlphaIndex) {
            var alphaState = preSumIndexToStateMap[currentSelectedAlphaIndex]!!
            preSumIndexToStateMap.values.forEach {
                it.state.value = false
            }
            alphaState.state.value = true
        }
    }
}

@Composable
fun ContractTopBar() {
    val navController = LocalNavController.current
    TopBar(
        center = {
            Text("?????????", color = MaterialTheme.chattyColors.textColor)
        },
        end = {
            IconButton(
                onClick = {
                    navController.navigate(AppScreen.addFriends)
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.add_friend), "add_friends", tint = MaterialTheme.chattyColors.iconColor)
            }
        },
        backgroundColor = MaterialTheme.chattyColors.backgroundColor
    )
}

@Composable
fun FriendItem(
    avatarRes: Int,
    friendName: String,
    motto: String,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.chattyColors.backgroundColor
    ) {
        CenterRow(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp)
        ) {
            CircleShapeImage(60.dp, painter = painterResource(id = avatarRes))
            Spacer(Modifier.padding(horizontal = 10.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = friendName,
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.chattyColors.textColor
                )
                Spacer(Modifier.padding(vertical = 3.dp))
                Text(
                    text = motto,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.chattyColors.textColor
                )
            }
        }
    }
}

@Composable
fun AlphaGuildBar(alphaStates: MutableCollection<AlphaState>, updateSelectIndex: (Int) -> Unit) {
    var currentIndex by remember { mutableStateOf(-1) }
    val alphaItemHeight = 28.dp
    val density = LocalDensity.current
    Column(
        modifier = Modifier.pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                with(density) {
                    currentIndex = (change.position.y / alphaItemHeight.toPx()).toInt().coerceIn(0, alphaStates.size - 1)
                }
            }
        }
    ) {
        alphaStates.forEachIndexed { index: Int, alphaState: AlphaState ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(if (alphaState.state.value) green else Color.Transparent)
                    .clickable {
                        currentIndex = index
                    }
            ) {
                Text(text = alphaState.alpha.toString(), color = if (alphaState.state.value || !MaterialTheme.chattyColors.isLight) Color.White else Color.Black)
            }
        }
    }
    LaunchedEffect(currentIndex) {
        if (currentIndex == -1) {
            return@LaunchedEffect
        }
        updateSelectIndex(currentIndex)
    }
}


//@Preview
//@Composable
//fun GuildBarDemo() {
//    AlphaGuildBar(mutableSetOf('a', 'c', 'd', 'e', 'f', 'h', 'n', 'v', 's'))
//}