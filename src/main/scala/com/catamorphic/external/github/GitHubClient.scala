package com.catamorphic
package external
package github

import playz._
import github.json._
import play.api.libs.ws.WS
import play.api.libs.ws.WS.WSRequestHolder
import play.api.libs.ws.Response
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation._
import java.net.URI
import scala.concurrent._
import scalaz._
import Scalaz._
import effect._
import syntax.id._
import java.util.Date

case class GHRepository(name: String
                      , owner: GHOwner
                      , description: String
                      , url: URI
                      , git_url: URI
                      , `private`: Boolean
                      , fork: Boolean)

case class GHOwner(login: String
                 , id: Int
                 , avatar_url: URI
                 , url: URI)

case class GHOrg(login: String
               , id: Int
               , avatar_url: URI
               , url: URI)

case class GHHookParams(name: String
                      , config: Map[String, String]
                      , events: List[String]
                      , active: Boolean)

case class GHHook(id: Long
                , url: URI
                , name: String
                , config: Map[String, String]
                , events: List[String]
                , active: Boolean)

case class GHPushReceive(before: String
                       , after: String
                       , ref: String
                       , commits: List[GHCommit])

case class GHPrReceive(action: GHPrAction
                     , number: Int
                     , pull_request: GHPullRequest)

case class GHPullRequest(url: URI
                       , title: String) // TODO add the rest of the fields defined in http://developer.github.com/v3/pulls/#get-a-single-pull-request

case class GHCommit(id: String // this is a sha1, but it's inconsistently called 'id' here
                  , message: String
                  , timestamp: Date
                  , added: List[String]
                  , removed: List[String]
                  , modified: List[String]
                  , author: GHUser)

case class GHError(code: GHValidationCode
                 , message: String
                 , resource: String)

case class GHErrors(errors: List[GHError]
                  , message: String)


abstract sealed class GHPrAction
case object GHPrOpened extends GHPrAction
case object GHPrClosed extends GHPrAction
case object GHPrSynchronize extends GHPrAction
case object GHPrReopened extends GHPrAction

abstract sealed class GHValidationCode
case object Missing extends GHValidationCode
case object MissingField extends GHValidationCode
case object Invalid extends GHValidationCode
case object AlreadyExists extends GHValidationCode
case class  Custom(code: String) extends GHValidationCode

case class GHFile(filename: String
                , additions: Int
                , deletions: Int
                , changes: Int
                , status: String
                , raw_url: URI
                , blob_url: URI
                , contents_url: URI
                , patch: String)

case class GHFileContents(sha: String
                        , size: Int
                        , name: String
                        , path: String
                        , url: URI
                        , content: String)


case class GHShaRef(url: URI
                  , sha: String)

case class GHExpandedCommit(url: URI
                          , sha: String
                          , files: List[GHFile]
                          , tree: GHShaRef
                          , parents: List[GHShaRef])

case class GHCommitComment(body: String
                         , commit_id: String
                         , path: Option[String]
                         , line: Option[Int]
                         , position: Option[Int] = None
                         , url: Option[URI] = None)

case class GHUser(name: String, email: String)

case class GHTree(sha: String
                , url: URI
                , tree: List[GHTreeNode])

case class GHTreeNode(mode: String
                    , `type`: GHGitObjectType
                    , sha: String
                    , path: String
                    , url: URI)

abstract sealed class GHGitObjectType
case object GHCommitType extends GHGitObjectType
case object GHBlobType extends GHGitObjectType
case object GHTreeType extends GHGitObjectType
case object GHTagType extends GHGitObjectType


object GitHubClient {
  import ExecutionContext.Implicits.global

  private def gh(path: String): WSRequestHolder = WS.url("https://api.github.com" + path)

  private class RichWSRequestHolder(h: WSRequestHolder) {
    def authWith(token: String): WSRequestHolder = h.withQueryString(("access_token", token))
  }

  private implicit def enrichHolder(h: WSRequestHolder) = new RichWSRequestHolder(h)

  def postComment(token: String, owner: String, repoName: String, sha: String, comment: GHCommitComment) = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/commits{/sha1}/comments").params("owner" -> owner, "repo" -> repoName, "sha1" -> sha)
    respToJson[GHCommitComment](gh(uri.toASCIIString).authWith(token).post[JsValue](writeJson(comment)))
  }

  def fetchContents(token: String, file: GHFile): AsyncJsResult[GHFileContents] = 
    fetchContents(token, file.contents_url)
  
  def fetchContents(token: String, contents_url: URI, ref: Option[String] = None): AsyncJsResult[GHFileContents] = {
    val params: List[(String, String)] = ref.map("ref" -> _).toList 
    respToJson[GHFileContents](WS.url(contents_url.toASCIIString).authWith(token).withQueryString(params: _*).get())    
  }

  def fetchPrCommits(token: String, owner: String, repoName: String, number: Int): AsyncJsResult[List[GHShaRef]] = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/pulls{/number}/commits").params("owner" -> owner, "repo" -> repoName, "number" -> number.toString)
    respToJson[List[GHShaRef]](gh(uri.toASCIIString).authWith(token).get())    
  }

  def fetchExpandedCommit(token: String, owner: String, repoName: String, sha1: String): AsyncJsResult[GHExpandedCommit] = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/commits{/sha1}").params("owner" -> owner, "repo" -> repoName, "sha1" -> sha1)
    respToJson[GHExpandedCommit](gh(uri.toASCIIString).authWith(token).get())
  } 

  def fetchTree(token: String, owner: String, repoName: String, sha1: String): AsyncJsResult[GHTree] = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/git/trees{/sha1}").params("owner" -> owner, "repo" -> repoName, "sha1" -> sha1)
    respToJson[GHTree](gh(uri.toASCIIString).authWith(token).get())
  }

  def fetchTreeRecursive(token: String, owner: String, repoName: String, sha1: String): AsyncJsResult[GHTree] = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/git/trees{/sha1}?recursive=1").params("owner" -> owner, "repo" -> repoName, "sha1" -> sha1)
    respToJson[GHTree](gh(uri.toASCIIString).authWith(token).get())
  }

  def fetchUserRepositories(token: String): AsyncJsResult[Stream[GHRepository]] =
    respToJson[Stream[GHRepository]](gh("/user/repos").authWith(token).get())

  def fetchOrgRepositories(token: String, orgName: String): AsyncJsResult[Stream[GHRepository]] = {
    val uri: URI = UriTemplate("/orgs{/org}/repos").params("org" -> orgName)
    respToJson[Stream[GHRepository]](gh(uri.toASCIIString).authWith(token).get()) 
  }

  def fetchOrgs(token: String): AsyncJsResult[Stream[GHOrg]] = {
    respToJson[Stream[GHOrg]](gh("/user/orgs").authWith(token).get())
  } 

  def createHook(token: String, owner: String, repoName: String, hook: GHHookParams): AsyncJsResult[GHErrors \/ GHHook] = {
    val uri: URI = UriTemplate("/repos{/owner,repo}/hooks").params("owner" -> owner, "repo" -> repoName)
    eitherRespToJson[GHErrors, GHHook](gh(uri.toASCIIString).authWith(token).post[JsValue](writeJson(hook)))
  } 
}