package com.catamorphic
package external
package github

import playz._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.data.validation.ValidationError

import java.net.URI

package object json extends GitHubClientReadsInstances with GitHubClientWritesInstances {
  
}

trait GitHubClientReadsInstances {

  implicit val ghOrgReads: Reads[GHOrg] = Json.reads[GHOrg]
  implicit val ghOwnerReads: Reads[GHOwner] = Json.reads[GHOwner]
  implicit val ghRepositoryReads: Reads[GHRepository] = Json.reads[GHRepository]

  implicit val ghPrActionReads: Reads[GHPrAction] = new Reads[GHPrAction] {
    def reads(json: JsValue) = {
      lazy val error = JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.token", json)))) 
      json match {
        case JsString(s) => s match {
          case "opened"        => JsSuccess(GHPrOpened)
          case "closed"        => JsSuccess(GHPrClosed)
          case "synchronize"   => JsSuccess(GHPrSynchronize)
          case "reopened"      => JsSuccess(GHPrReopened)
          case _               => error
        }
        case v => error
      }    
    }    

  }    

  implicit val ghPullRequestReads: Reads[GHPullRequest] = Json.reads[GHPullRequest]

  implicit val ghHookReads: Reads[GHHook] = Json.reads[GHHook]

  implicit val ghUserReads: Reads[GHUser] = Json.reads[GHUser]
  implicit val ghCommitReads: Reads[GHCommit] = Json.reads[GHCommit]
  implicit val ghPushReceiveReads: Reads[GHPushReceive] = Json.reads[GHPushReceive]
  implicit val ghPrReceiveReads: Reads[GHPrReceive] = Json.reads[GHPrReceive]

  implicit val ghFileContentsReads: Reads[GHFileContents] = Json.reads[GHFileContents]

  implicit val ghValidationCodeReads: Reads[GHValidationCode] = new Reads[GHValidationCode] {
    def reads(json: JsValue) = {
      lazy val error = JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.token", json)))) 
      json match {
        case JsString(s) => s match {
          case "missing"        => JsSuccess(Missing)
          case "missing_field"  => JsSuccess(MissingField)
          case "invalid"        => JsSuccess(Invalid)
          case "already_exists" => JsSuccess(AlreadyExists)
          case _                => JsSuccess(Custom(s))
        }
        case v => error
      }    
    }    
  }

  implicit val ghFileReads: Reads[GHFile] = Json.reads[GHFile]
  implicit val ghShaRefReads: Reads[GHShaRef] = Json.reads[GHShaRef]
  implicit val ghExpandedCommitReads: Reads[GHExpandedCommit] = Json.reads[GHExpandedCommit] 
  implicit val ghCommitCommentReads: Reads[GHCommitComment] = Json.reads[GHCommitComment]
  implicit val ghErrorReads: Reads[GHError] = Json.reads[GHError]
  implicit val ghErrorsReads: Reads[GHErrors] = Json.reads[GHErrors]

  implicit val ghGitObjectTypeReads: Reads[GHGitObjectType] = new Reads[GHGitObjectType] {
    def reads(json: JsValue) = {
      lazy val error = JsError(Seq(JsPath() -> Seq(ValidationError("validate.error.expected.token", json)))) 
      json match {
        case JsString(s) => s match {
          case "blob"        => JsSuccess(GHBlobType)
          case "commit"      => JsSuccess(GHCommitType)
          case "tree"        => JsSuccess(GHTreeType)
          case "tag"         => JsSuccess(GHTagType)
          case _                => error
        }
        case v => error
      }    
    }    
  }

  implicit val ghTreeNodeReads: Reads[GHTreeNode] = Json.reads[GHTreeNode]
  implicit val ghTreeReads: Reads[GHTree] = Json.reads[GHTree]
}

trait GitHubClientWritesInstances {
  implicit val ghHookParamsWrites: Writes[GHHookParams] = Json.writes[GHHookParams]
  implicit val ghCommitCommentWrites: Writes[GHCommitComment] = Json.writes[GHCommitComment]
}