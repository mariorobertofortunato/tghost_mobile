package com.mrf.tghost.data.network.mappers

import com.mrf.tghost.data.network.model.metadata.CreatorDto
import com.mrf.tghost.domain.model.metadata.Creator
import com.mrf.tghost.domain.model.metadata.TokenOnChainMetadata
import org.sol4k.PublicKey
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun CreatorDto.toDomainModel(): Creator {
    return Creator(
        address = address,
        verified = verified,
        share = share
    )
}

fun mapOnChainMetadata(data: ByteArray): TokenOnChainMetadata? {
    if (data.isEmpty()) return null
    try {
        val buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN)
        val key = buffer.get().toUByte()
        val updateAuthorityBytes = ByteArray(32)
        buffer.get(updateAuthorityBytes)
        val updateAuthority = PublicKey(updateAuthorityBytes).toBase58()
        val mintBytes = ByteArray(32)
        buffer.get(mintBytes)
        val mint = PublicKey(mintBytes).toBase58()

        val nameLength = buffer.int
        if (nameLength < 0 || nameLength > data.size - buffer.position()) {
            return null
        }
        val nameBytes = ByteArray(nameLength)
        buffer.get(nameBytes)
        val name = nameBytes.toString(Charsets.UTF_8).trimEnd('\u0000')

        val symbolLength = buffer.int
        if (symbolLength < 0 || symbolLength > data.size - buffer.position()) {
            return null
        }
        val symbolBytes = ByteArray(symbolLength)
        buffer.get(symbolBytes)
        val symbol = symbolBytes.toString(Charsets.UTF_8).trimEnd('\u0000')

        val uriLength = buffer.int
        if (uriLength < 0 || uriLength > data.size - buffer.position()) {
            return null
        }
        val uriBytes = ByteArray(uriLength)
        buffer.get(uriBytes)
        val uri = uriBytes.toString(Charsets.UTF_8).trimEnd('\u0000')

        if (buffer.remaining() < 2) {
            return null
        }
        val sellerFeeBasisPoints = buffer.short.toUShort()

        if (buffer.remaining() < 1) {
            return null
        }
        val creatorsOption = buffer.get().toInt()
        var creatorsList: List<CreatorDto>? = null
        if (creatorsOption == 1) {
            if (buffer.remaining() < 4) {
                return null
            }
            val numCreators = buffer.int
            if (numCreators !in 0..100) {
                return null
            }
            creatorsList = mutableListOf()
            for (i in 0 until numCreators) {
                if (buffer.remaining() < 32 + 1 + 1) {
                    return null
                }
                val creatorAddressBytes = ByteArray(32)
                buffer.get(creatorAddressBytes)
                val verified = buffer.get().toInt() == 1
                val share = buffer.get().toUByte()
                creatorsList.add(
                    CreatorDto(
                        PublicKey(creatorAddressBytes).toBase58(),
                        verified,
                        share
                    )
                )
            }
        }

        var primarySaleHappened: Boolean? = null
        if (buffer.hasRemaining() && buffer.get().toInt() == 1) {
            if (buffer.hasRemaining()) {
                primarySaleHappened = buffer.get().toInt() == 1
            } else {
                return null
            }
        }

        var isMutable: Boolean? = null
        if (buffer.hasRemaining() && buffer.get().toInt() == 1) {
            if (buffer.hasRemaining()) {
                isMutable = buffer.get().toInt() == 1
            } else {
                return null
            }
        }

        return TokenOnChainMetadata(
            key = key,
            updateAuthority = updateAuthority,
            mint = mint,
            name = name,
            symbol = symbol,
            uri = uri,
            sellerFeeBasisPoints = sellerFeeBasisPoints,
            creators = creatorsList?.map { it.toDomainModel() },
            primarySaleHappened = primarySaleHappened,
            isMutable = isMutable
        )

    } catch (e: BufferUnderflowException) {
        return null
    } catch (e: Exception) {
        return null
    }
}
