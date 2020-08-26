package com.alavpa.bsproducts.domain.interactors

import com.alavpa.bsproducts.domain.model.Product

class ProductMockBuilder {

    private var product = Product(
        0,
        "",
        "",
        "",
        0,
        "",
        0,
        "",
        0
    )

    fun id(id: Long): ProductMockBuilder {
        product = product.copy(id = id)
        return this
    }

    fun price(price: Int): ProductMockBuilder {
        product = product.copy(price = price)
        return this
    }

    fun discount(percentage: Int): ProductMockBuilder {
        product = product.copy(discountPercentage = percentage)
        return this
    }

    fun stock(stock: Int): ProductMockBuilder {
        product = product.copy(stock = stock)
        return this
    }

    fun description(description: String): ProductMockBuilder {
        product = product.copy(description = description)
        return this
    }

    fun name(name: String): ProductMockBuilder {
        product = product.copy(name = name)
        return this
    }

    fun brand(brand: String): ProductMockBuilder {
        product = product.copy(brand = brand)
        return this
    }

    fun currency(currency: String): ProductMockBuilder {
        product = product.copy(currency = currency)
        return this
    }

    fun image(image: String): ProductMockBuilder {
        product = product.copy(image = image)
        return this
    }

    fun build(): Product {
        return product
    }
}
