package models

case class PreOrder(accountId: Long, symbol: Symbol)

sealed abstract class Order(id: Long, accountId: Long, symbol: Symbol)

case class Limit(
    id: Long,
    accountId: Long,
    symbol: Symbol,
    price: BigDecimal,
    quantity: Long,
    amount: BigDecimal,
    surplus: Long,
    knockdown: Long,
    a: Option[Long]
) extends Order(id, accountId, symbol)
