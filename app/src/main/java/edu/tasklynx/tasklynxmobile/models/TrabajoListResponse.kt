import edu.tasklynx.tasklynxmobile.models.Trabajo

data class TrabajoListResponse(
    val result: List<Trabajo>,
    val error: Boolean = false
)