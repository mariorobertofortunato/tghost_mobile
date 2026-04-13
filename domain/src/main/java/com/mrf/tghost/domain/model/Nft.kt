package com.mrf.tghost.domain.model

/**
 * A rich domain model for a Non-Fungible Token (NFT).
 *
 * This class abstracts the core concepts of an NFT across different blockchains (EVM, Solana, etc.)
 * and provides a unified, clean representation for the business logic and UI layers.
 */
data class Nft(
    // Core Identification
    val id: String, // Mint address on Solana, tokenId on EVM
    val address: String, // contract address on EVM, mint address on solana
    val name: String?,
    val chain: Chain,

    // Display & Metadata
    val description: String?,
    val imageUrl: String?,
    val animationUrl: String?,
    val externalUrl: String?,
    val attributes: List<NftAttribute> = emptyList(),

    // Collection Information
    val collectionName: String?,
    val collectionSymbol: String?,
    val collectionAddress: String?,

    // Ownership & Supply
    val owner: String?,
    val contractType: String?, // e.g., ERC721, ERC1155
    val amount: String?,

    // Verification & Status
    val isVerified: Boolean = false,
    val isSpam: Boolean = false
)

/**
 * Represents a single attribute of an NFT, typically for display.
 */
data class NftAttribute(
    val traitType: String,
    val value: String,
    val displayType: String? = null
)
