package id.teman.app.mitra.domain.model.chat


@kotlinx.serialization.Serializable
data class ChatMessageSpec(
    val isSelfMessage: Boolean,
    val sendTime: String,
    val message: String,
)