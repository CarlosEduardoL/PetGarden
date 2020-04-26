package zero.network.petgarden.model.entity

class Duration (
    val start: Long,
    val end: Long,
    val cost: Int
): Comparable<Duration> {
    fun extractSlice(duration: Duration): List<Duration>{
        return if (duration.end == end && duration.start == start){
            emptyList()
        }else if (duration.end == end){
            listOf(Duration(start, duration.start, cost))
        }else if (duration.start == start){
            listOf(Duration(duration.end,end, cost))
        }else{
            listOf(
                Duration(start, duration.start, cost),
                Duration(duration.end, end, cost)
            )
        }
    }

    fun contains(d: Duration) = d.start > start && d.end < end

    fun collide(d: Duration): Boolean {
        return d.start <= start && d.end >= start
                || d.start >= start && d.end <= end
                || d.start <= end && d.end >= end
                || d.start <= start && d.end >= end
    }

    override fun compareTo(other: Duration): Int {
        return start.compareTo(other.start)
    }

}
