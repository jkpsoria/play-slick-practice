package models.repo

import scala.concurrent.ExecutionContext
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.domain.Account
import java.util.UUID
import scala.concurrent.Future

trait AccountRepository {
    def createAcc(account: Account): Future[Int]
    def findAcc(id: UUID): Future[Option[Account]]
    def getAcc(email: String): Future[Seq[Account]]
    def deleteAcc(id: UUID): Future[Int]
    def updateAccount(id: UUID, newAccount: Account): Future[Int]
}

@Singleton
final class AccountRepo @Inject()
(protected val dbConfigProvider: DatabaseConfigProvider)
(implicit ex: ExecutionContext) 
extends HasDatabaseConfigProvider[JdbcProfile] 
with AccountRepository {
    import profile.api._

    protected class Accounts (tag: Tag) extends Table[Account](tag, "ACCOUNTS") {
        def id = column[UUID]("ID", O.PrimaryKey)
        def email = column[String]("EMAIL")

        // def * = (id, email) <> (Account.tupled, Account.unapply)
        def * = (id, email).mapTo[Account]
    }
    val accounts = TableQuery[Accounts]

    def createSchema = db.run(accounts.schema.createIfNotExists)
    override def createAcc(account: Account): Future[Int] = db.run(accounts += account)
    override def findAcc(id: UUID): Future[Option[Account]] = db.run(accounts.filter(_.id === id).result.headOption)
    //override def getAcc(email: String): Future[Seq[Account]] = db.run(accounts.filter(_.email like s"%$email%").result)
    override def getAcc(email: String): Future[Seq[Account]] = db.run(accounts.filter(_.email.like(s"%$email%")).result)
    override def deleteAcc(id: UUID): Future[Int] = db.run(accounts.filter(_.id === id).delete)
    override def updateAccount(id: UUID, newAccount: Account): Future[Int] = db.run(accounts.filter(_.id === id).update(newAccount))

    def getAll(): Future[Seq[Account]] = db.run(accounts.result)
}