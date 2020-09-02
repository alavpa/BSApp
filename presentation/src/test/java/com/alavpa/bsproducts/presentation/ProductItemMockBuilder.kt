package com.alavpa.bsproducts.presentation

import com.alavpa.bsproducts.presentation.model.ProductItem

class ProductItemMockBuilder {

    private var product = ProductItem(
        0,
        "",
        "",
        0,
        "",
        "",
        false
    )

    fun id(id: Long): ProductItemMockBuilder {
        product = product.copy(id = id)
        return this
    }

    fun price(price: Int): ProductItemMockBuilder {
        product = product.copy(price = price)
        return this
    }

    fun name(name: String): ProductItemMockBuilder {
        product = product.copy(name = name)
        return this
    }

    fun brand(brand: String): ProductItemMockBuilder {
        product = product.copy(brand = brand)
        return this
    }

    fun currency(currency: String): ProductItemMockBuilder {
        product = product.copy(currency = currency)
        return this
    }

    fun image(image: String): ProductItemMockBuilder {
        product = product.copy(image = image)
        return this
    }

    fun liked(liked: Boolean): ProductItemMockBuilder {
        product = product.copy(liked = liked)
        return this
    }

    fun build(): ProductItem {
        return product
    }
}
