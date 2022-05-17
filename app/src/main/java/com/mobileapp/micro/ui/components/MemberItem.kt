package com.mobileapp.micro.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mobileapp.micro.R
import com.mobileapp.micro.model.User

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemberItem(
    email: String,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            text = email,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .clickable { onDelete() },
            imageVector = Icons.Outlined.Delete,
            contentDescription = "Delete"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MemberItem(
    user: User,
    isChecked: MutableState<Boolean>? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isChecked != null) {
                    isChecked.value = !isChecked.value
                }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultImage(
            modifier = Modifier
                .padding(8.dp)
                .size(35.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape),
            localUri = user.localPhotoUri,
            onlineUri = user.onlinePhotoUri,
            placeHolder = R.drawable.sample_avatar,
            error = R.drawable.sample_avatar,
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1f),
            text = user.displayName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(16.dp))
        if (isChecked != null) {
            if (isChecked.value) {
                Icon(
                    modifier = Modifier.padding(8.dp),
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null
                )
            }
        }
    }
}