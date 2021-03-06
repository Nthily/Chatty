package com.chatty.compose.screens.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.chatty.compose.R
import com.chatty.compose.bean.UserProfileData
import com.chatty.compose.screens.chatty.mock.friends
import com.chatty.compose.ui.components.AppScreen
import com.chatty.compose.ui.components.CenterRow
import com.chatty.compose.ui.components.HeightSpacer
import com.chatty.compose.ui.components.WidthSpacer
import com.chatty.compose.ui.theme.chattyColors
import com.chatty.compose.ui.utils.LocalNavController


@Composable
fun PersonalProfile() {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.chattyColors.backgroundColor),
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .paint(
                        painterResource(id = R.drawable.google_bg),
                        contentScale = ContentScale.FillBounds
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                PersonalProfileHeader()
            }
            HeightSpacer(value = 10.dp)
            PersonalProfileDetail()
        }
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomSettingIcons()
        }
    }
}


fun getCurrentLoginUserProfile(): UserProfileData {
    return friends[0]
}

@Composable
fun PersonalProfileHeader() {
    var currentUser = getCurrentLoginUserProfile()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(10.dp)
    ) {
        val (portraitImageRef, usernameTextRef, desTextRef) = remember { createRefs() }
        Image(
            painter = painterResource(id = currentUser.avatarRes),
            contentDescription = "portrait",
            modifier = Modifier
                .constrainAs(portraitImageRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                .clip(CircleShape)
        )
        Text(
            text = currentUser.nickname,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Left,
            color = Color.White,
            modifier = Modifier
                .constrainAs(usernameTextRef) {
                    top.linkTo(portraitImageRef.top, 5.dp)
                    start.linkTo(portraitImageRef.end, 10.dp)
                    width = Dimension.preferredWrapContent
                }
        )
        Text(
            text = currentUser.motto,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier
                .constrainAs(desTextRef) {
                    top.linkTo(usernameTextRef.bottom, 10.dp)
                    start.linkTo(portraitImageRef.end, 10.dp)
                    end.linkTo(parent.end)
                    width = Dimension.preferredWrapContent
                }
        )
    }
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PersonalProfileDetail() {
    var currentUser = getCurrentLoginUserProfile()
    val navController = LocalNavController.current
    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        Text(
            text = "????????????",
            fontSize = 25.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.chattyColors.textColor,
            modifier = Modifier.padding(start = 20.dp),
        )
        Column(modifier = Modifier.fillMaxWidth()) {
            ProfileDetailRowItem(label = "??????", content = currentUser.gender ?: "??????") {
                navController.navigate("${AppScreen.profileEdit}/gender")
            }
            ProfileDetailRowItem(label = "??????", content = currentUser.age.toString() ?: "??????") {
                navController.navigate("${AppScreen.profileEdit}/age")
            }
            ProfileDetailRowItem(label = "?????????", content = currentUser.phone ?: "??????") {
                navController.navigate("${AppScreen.profileEdit}/phone")
            }
            ProfileDetailRowItem(label = "????????????", content = currentUser.email ?: "??????") {
                navController.navigate("${AppScreen.profileEdit}/email")
            }
            ProfileDetailRowItem(label = "?????????", isQrCode = true)  {
                navController.navigate("${AppScreen.profileEdit}/qrcode")
            }
        }
    }
}


@Composable
fun ProfileDetailRowItem(label: String, content: String = "", isQrCode: Boolean = false, onClick: () -> Unit = {}) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() }
            .padding(vertical = 30.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.chattyColors.textColor
            )
            Row {
                if (isQrCode) {
                    Icon(
                        painter = painterResource(id = R.drawable.qr_code),
                        contentDescription = label,
                        tint = MaterialTheme.chattyColors.iconColor
                    )
                } else {
                    Text(
                        text = content,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.chattyColors.textColor,
                    )
                }
                Icon(painter = painterResource(
                    id = R.drawable.expand_right),
                    contentDescription = label,
                    tint = MaterialTheme.chattyColors.iconColor
                )
            }
        }
    }
}

@Composable
fun BottomSettingIcons() {
    var chattyColors = MaterialTheme.chattyColors
    CenterRow(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(80.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row {
            IconButton(
                onClick = {}
            ) {
                Column(
                    modifier = Modifier.size(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.settings), contentDescription = null, tint = MaterialTheme.chattyColors.iconColor)
                    HeightSpacer(value = 4.dp)
                    Text(text = "??????", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.chattyColors.textColor)
                }
            }
            WidthSpacer(value = 10.dp)
            IconButton(
                onClick = {
                    chattyColors.toggleTheme()
                }
            ) {
                Column(
                    modifier = Modifier.size(60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.dark_mode),
                        contentDescription = null, tint = MaterialTheme.chattyColors.iconColor
                    )
                    HeightSpacer(value = 4.dp)
                    Text(
                        text = if (chattyColors.isLight) "????????????" else "????????????",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.chattyColors.textColor
                    )
                }
            }
        }

        IconButton(
            onClick = { }
        ) {
            Column(
                modifier = Modifier.size(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(painter = painterResource(id = R.drawable.logout), contentDescription = null, tint = MaterialTheme.chattyColors.iconColor)
                HeightSpacer(value = 4.dp)
                Text(text = "??????", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.chattyColors.textColor)
            }
        }
    }
}