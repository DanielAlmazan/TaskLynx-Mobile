import edu.tasklynx.tasklynxmobile.models.Trabajo

data class TrabajoResponse(
    val result: List<Trabajo>,
    val error: Boolean
)