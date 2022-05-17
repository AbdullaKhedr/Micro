package com.mobileapp.micro.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.mobileapp.micro.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class StorageRepository {

    private val storageRef = Firebase.storage.reference
    private val courseDisplayImagesPath = "course_display_images/"
    private val lessonMediaPath = "lessons_media/"
    private val studyGroupDisplayImagesPath = "study_group_display_images/"
    private val channelDisplayImagesPath = "channel_display_images/"
    private val userProfileImagesPath = "user_profile_images/"
    val courseDisplayImagesStorageRef = storageRef.child(courseDisplayImagesPath)
    val lessonMediaStorageRef = storageRef.child(lessonMediaPath)
    val studyGroupDisplayImagesStorageRef = storageRef.child(studyGroupDisplayImagesPath)
    val channelDisplayImagesStorageRef = storageRef.child(channelDisplayImagesPath)
    val userProfileImagesStorageRef = storageRef.child(userProfileImagesPath)
    private val dispatchers = Dispatchers.IO

    /**
     * @return the Uri string of the uploaded file.
     */
    suspend fun uploadMedia(
        storageRef: StorageReference,
        fileName: String,
        uri: String
    ): String =
        withContext(dispatchers) {
            var downloadUri = ""
            val ref = storageRef.child(fileName)
            ref.putFile(Uri.parse(uri))
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    ref.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        downloadUri = task.result.toString()
                    }
                }.await()
            downloadUri
        }

    suspend fun getMediaByFileName(
        storageRef: StorageReference,
        fileName: String
    ): Uri = withContext(dispatchers) {
        storageRef.child(fileName).downloadUrl.await()
    }

    suspend fun downloadMediaToFile(
        storageRef: StorageReference,
        fileName: String
    ): String = withContext(dispatchers) {
        val localFile = getTempFile(fileName)
        val localFileUri = ""
        storageRef.child(fileName).getFile(localFile)
            .addOnSuccessListener {
                // Local temp file has been created
                // TODO save the tempFile, and get the uri
            }.addOnFailureListener {
                // Handle any errors
            }.await()
        localFileUri
    }

    // Returns a temp file with from a given fileName
    private fun getTempFile(fileName: String): File {
        val name = fileName.reversed().split('.', limit = 2)
        val prefix = name[1].reversed()
        val suffix = name[0].reversed()
        return File.createTempFile(prefix, suffix)
    }

    // Adds the time to the name and
    // assign a unique identifier if the filename is empty
    fun generateMediaFileName(name: String, mediaType: MediaType): String {
        return if (name.isEmpty()) { // In case the media has no name
            return when (mediaType) {
                MediaType.IMAGE -> System.currentTimeMillis().toString() + ".jpg"
                MediaType.VIDEO -> System.currentTimeMillis().toString() + ".mp4"
                else -> System.currentTimeMillis().toString()
            }
        } else {
            System.currentTimeMillis().toString() + "_" + name
        }
    }

    suspend fun deleteMedia(storageRef: StorageReference, fileName: String) {
        withContext(dispatchers) {
            storageRef.child(fileName).delete().await()
        }
    }
}