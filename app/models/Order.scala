package models

case class Order(var id: Long, accountId: Long, symbol: Symbol)

case class Limit(
    override var id: Long,
    override val accountId: Long,
    override val symbol: Symbol,
    price: BigDecimal,
    quantity: Long,
    amount: BigDecimal,
    surplus: Long,
    knockdown: Long
) extends Order(id, accountId, symbol)
