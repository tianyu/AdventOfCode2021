package io

inline fun buildStructuredString(action: StructuredWriter.() -> Unit): String =
  StructuredWriter().apply(action).toString()

class StructuredWriter(val newline: String = "\n", val newlineIndent: String = "$newline  ", val buffer: Appendable = StringBuilder()): Appendable by buffer {
  fun indented(action: StructuredWriter.() -> Unit) =
    StructuredWriter(newlineIndent, buffer = this).apply(action)

  fun <T> join(elements: Iterable<T>, separator: String = ", ", write: StructuredWriter.(T) -> Unit) {
    val iterator = elements.iterator()
    if (!iterator.hasNext()) return
    write(iterator.next())
    while (iterator.hasNext()) {
      append(separator)
      write(iterator.next())
    }
  }

  override fun toString(): String = buffer.toString()
}