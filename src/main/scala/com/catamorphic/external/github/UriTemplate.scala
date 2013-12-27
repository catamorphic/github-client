package com.catamorphic
package external
package github

import com.damnhandy.uri.template.{UriTemplate => JUriTemplate}
import java.net.URI

class UriTemplate private (template: JUriTemplate) {
  import scala.collection.JavaConverters._
  def params(p: (String, String)*): URI = URI.create(template.expand(collection.mutable.Map[String, Object](p: _*).asJava))
}

object UriTemplate {
  def apply(s: String) = new UriTemplate(JUriTemplate.fromTemplate(s))
}