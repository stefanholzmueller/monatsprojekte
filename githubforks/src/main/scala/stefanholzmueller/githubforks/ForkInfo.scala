package stefanholzmueller.githubforks

import java.net.URL
import java.util.concurrent.TimeUnit

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import org.json4s.DefaultFormats
import org.json4s.JsonAST.JInt
import org.json4s.JsonAST.JValue
import org.json4s.jvalue2extractable
import org.json4s.jvalue2monadic
import org.json4s.native.JsonMethods.parse
import org.json4s.string2JsonInput

import com.stackmob.newman.ApacheHttpClient
import com.stackmob.newman.dsl.GET
import com.stackmob.newman.dsl.transformerToHttpRequest
import com.stackmob.newman.response.HttpResponseCode.httpResponseCodeToInt

object ForkInfo extends App {

  case class ForkInfo(fork: String, commits: Int, stars: Int)

  val user = "denniskaselow" 
  val repo = "dartemis"

  val info = for {
    fork <- user::githubForks(user, repo)
    commits = githubCommitCount(fork, repo)
    stars = githubStarCount(fork, repo)
  } yield ForkInfo(fork, commits, stars)
  info.foreach { case ForkInfo(f, c, s) => println(s"$f: $c commits, $s stars") }
  sys.exit()


  def githubForks(user: String, repo: String) = {
	  implicit val formats = DefaultFormats
    val url = s"https://api.github.com/repos/${user}/${repo}/forks"
    val json = httpGet(url)
    (json \ "owner" \ "login").extract[List[String]]
  }

  def githubCommitCount(user: String, repo: String) = {
    val url = s"https://api.github.com/repos/${user}/${repo}/stats/contributors"
    val json = httpGet(url)
    (for { JInt(contributorCommits) <- json \ "total" } yield contributorCommits).sum.toInt
  }

  def githubStarCount(user: String, repo: String) = {
	  implicit val formats = DefaultFormats
    val url = s"https://api.github.com/repos/${user}/${repo}"
    val json = httpGet(url)
    (json \ "stargazers_count").extract[Int]
  }

  def httpGet(url: String): JValue = {
    println("HTTP GET " + url)
    val response = Await.result(GET(new URL(url))(new ApacheHttpClient).apply, Duration(10, TimeUnit.SECONDS))
    if (response.code.toInt == 202) {
      Thread.sleep(10000)
      httpGet(url)
    } else {
      assert(response.code.toInt == 200, response)
      parse(response.bodyString)
    }
  }
}
