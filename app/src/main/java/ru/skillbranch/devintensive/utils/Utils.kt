package ru.skillbranch.devintensive.utils

import ru.skillbranch.devintensive.extensions.containsOneOf

object Utils {

    val rusLowerAlphabet = "а,б,в,г,д,е,ё,ж,з,и,й,к,л,м,н,о,п,р,с,т,у,ф,х,ц,ч,ш,щ,ъ,ы,ь,э,ю,я".split(",")
    val rusUpperAlphabet = "А,Б,В,Г,Д,Е,Ё,Ж,З,И,Й,К,Л,М,Н,О,П,Р,С,Т,У,Ф,Х,Ц,Ч,Ш,Щ,Ъ,Ы,Ь,Э,Ю,Я".split(",")
    val engLowerAlphabet = "a,b,v,g,d,e,e,zh,z,i,i,k,l,m,n,o,p,r,s,t,u,f,h,c,ch,sh,sh,,i,,e,yu,ya".split(",")
    val engUpperAlphabet = "A,B,V,G,D,E,E,Zh,Z,I,I,K,L,M,N,O,P,R,S,T,U,F,H,C,Ch,Sh,Sh,,I,,E,Yu,Ya".split(",")

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        val parts: List<String>? = fullName?.split(" ")
        var firstName = parts?.getOrNull(0)?.trim()
        var lastName = parts?.getOrNull(1)?.trim()

        if (firstName == "") firstName = null
        if (lastName == "") lastName = null

        return firstName to lastName
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var localText = payload

        for (i in rusLowerAlphabet.indices) {
            localText = localText.split(rusLowerAlphabet[i]).joinToString(engLowerAlphabet[i])
            localText = localText.split(rusUpperAlphabet[i]).joinToString(engUpperAlphabet[i])
        }

        return localText.trim().split(" ").joinToString(divider)
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val first = if (firstName?.trim() == "") null else firstName?.trim()
        val last = if (lastName?.trim() == "") null else lastName?.trim()

        return if (first == null && last == null) {
            null
        } else if (first == null) {
            "${last?.get(0)?.toUpperCase()}"
        } else if (last == null) {
            "${first[0].toUpperCase()}"
        } else "${first[0].toUpperCase()}${last[0].toUpperCase()}"
    }

    fun isValidRepository(rep: String): Boolean {
        if (rep == "") {
            return true
        }

        val githubStr = "github.com"

        val exceptions = listOf(
            "enterprise",
            "features",
            "topics",
            "collections",
            "trending",
            "events",
            "marketplace",
            "pricing",
            "nonprofit",
            "customer-stories",
            "security",
            "login",
            "join"
        )

        val prefixes = listOf(
            "", "https://", "www.", "https://www."
        )

        if (rep.containsOneOf(exceptions)) {
            return false
        }

        if (rep.contains(githubStr)) {
            val githubStartIndex = rep.indexOf(githubStr)

            if (!prefixes.contains(rep.substring(0, githubStartIndex))) {
                return false
            }

            val githubEndIndex = githubStartIndex + githubStr.length

            val nickWithSlash = rep.substring(startIndex = githubEndIndex)

            if (nickWithSlash == "" || nickWithSlash == "/") {
                return false
            }

            val nick = nickWithSlash.substringAfter("/")
            if (nick.contains("/")) {
                return false
            }

            return true
        }

        return false
    }
}