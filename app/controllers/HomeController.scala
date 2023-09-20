package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import java.util.UUID
import models.repo.AccountRepo
import models.domain.Account
import scala.concurrent.ExecutionContext
import views.html.helper.form

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(val controllerComponents: ControllerComponents, accountRepo: AccountRepo, implicit val ec: ExecutionContext) extends BaseController {
   val createAccountForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID()),
      "email" -> nonEmptyText
    )
    (Account.apply)(Account.unapply)
   )
  def index() = Action.async { implicit request =>
    accountRepo.createSchema.map(_ => Ok)
  }

  def create() = Action.async { implicit request => 
    createAccountForm.bindFromRequest().fold(
      formsWithError => Future.successful(BadRequest),
      account => accountRepo.createAcc(account.copy(id = UUID.randomUUID())).map(_ => Ok)
    )  
  }
  def getAcc(name: String) = Action.async { implicit request =>
    accountRepo.getAcc(name).map(accounts => Ok(accounts.mkString("\n")))
  }

  def getAll() = Action.async{ implicit request =>
    accountRepo.getAll().map(accounts => Ok(accounts.mkString("\n")))  
  }

  def deleteAcc(id: UUID) = Action.async { implicit request =>
    accountRepo.deleteAcc(id).map(_ => Ok)

  }

  // def updateAcc(id: UUID, newEmail: String) = Action.async { implicit request =>
  //   accountRepo.updateAcc(id, newEmail).map(_ => Ok)
  // }

  // def updateAcc(id: UUID, newEmail: String) = Action.async { implicit request =>
  //   createAccountForm.bindFromRequest().fold(
  //     formsWithError => {
  //       Future.successful(BadRequest("Something went wrong."))
  //     },
  //     account => {
  //       accountRepo.updateAcc(id, account.copy(id = id)).map(result => Ok(result.toString))
  //     }
  //   )
    
  // }

  def updateAcc(ids: UUID) = Action.async { implicit request =>
    createAccountForm.bindFromRequest().fold(
      formsWithError => Future.successful(BadRequest),
      account => accountRepo.updateAccount(ids, account.copy(id = ids)).map(_ => Ok)
    )
  }
}