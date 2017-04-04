package v1.post

import javax.inject.{Inject, Provider}
import com.mongodb.casbah.Imports._
import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json._

/**
  * DTO for displaying post information.
  */
case class PostResource(id: String, link: String, candidate: String, method: String, winner: String, table: String)

object PostResource {

  /**
    * Mapping to write a PostResource out as a JSON value.
    */
  implicit val implicitWrites = new Writes[PostResource] {
    def writes(post: PostResource): JsValue = {
      Json.obj(
        "id" -> post.id,
        "link" -> post.link,
        "candidate" -> post.candidate,
        "method" -> post.method,
        "winner" -> post.winner,
        "table" -> post.table
      )
    }
  }
}

/**
  * Controls access to the backend data, returning [[PostResource]]
  */
class PostResourceHandler @Inject()(
    routerProvider: Provider[PostRouter],
    postRepository: PostRepository)(implicit ec: ExecutionContext) {

  def create(postInput: PostFormInput): Future[PostResource] = {
    val data = PostData(PostId("999"), postInput.candidate,postInput.method,postInput.winner,postInput.table)
    // We don't actually create the post, so return what we have
    postRepository.create(data).map { id =>
      createPostResource(data)
    }
  }

  def lookup(id: String): Future[Option[PostResource]] = {
    val postFuture = postRepository.get(PostId(id))
    postFuture.map { maybePostData =>
      maybePostData.map { postData =>
        createPostResource(postData)
      }
    }
  }

  def find: Future[Iterable[PostResource]] = {
    postRepository.list().map { postDataList =>
      postDataList.map(postData => createPostResource(postData))
    }
  }

  private def createPostResource(p: PostData): PostResource = {
  val uri = MongoClientURI(${MONGODB_URI})
  val mongoClient =  MongoClient(uri)
  val db = mongoClient("semicolon")
  val collection = db("task")
  val dbObject = {
   val builder1 = MongoDBObject.newBuilder
   builder1 += "Id" -> p.id.toString
   builder1 += "Candidate" -> p.candidate
   builder1 += "Methods" -> p.method
   builder1 += "winner" -> p.winner
   builder1 += "Table" ->  p.table
   builder1.result
 }
 // insert the DBObject to MongoDB
  collection.save(dbObject)
    PostResource(p.id.toString, routerProvider.get.link(p.id), p.candidate, p.method, p.winner, p.table)
  }

}
