package articles.models

import commons.models.{Ordering, Username}

case class MainFeedPageRequest(tag: Option[String], author: Option[Username], favorited: Option[Username],
                               limit: Long, offset: Long, orderings: List[Ordering])