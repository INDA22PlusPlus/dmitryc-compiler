// Token related classes
open class Token(private val str: String) {
    override fun toString(): String {
        return str
    }
}

class Keyword(str: String): Token(str)

class MathOperator(str: String): Token(str)

class ComparisonOperator(str: String): Token(str)

class LogicOperator(str: String): Token(str)

class SpecialCharacter(str: String): Token(str)

class Variable(str: String): Token(str)

class EOL(str: String): Token(str) {
    override fun toString(): String {
        return "\\n"
    }
}

class CustomInt(str: String): Token(str) {
    private val value: Int = str.toInt()

    fun getValue(): Int{
        return value
    }
}

// Compiler related classes
class Lexer {
    // TODO: Maps and filters instead of for loops
    fun tokenize(str: String): MutableList<Token> {
        val tokens = mutableListOf<Token>()
        tokenizeInRows(str).map { row ->
            tokens.addAll(row)
            tokens.add(EOL("\n"))
        }
        return tokens
    }
    fun tokenizeInRows(str: String): MutableList<MutableList<Token>> {
        // Defining all keywords, symbols etc
        val keywords = listOf("if", "elif", "else", "while", "print")
//        val mathOperators = listOf("+", "-", "*", "/", "**", "%", "//")           // Long form
        val mathOperators = listOf("+", "-", "*", "/")                              // Short form
//        val comparisonOperators = listOf("==", "!=", ">", "<", ">=", "<=")        // Long form
        val comparisonOperators = listOf("==", "!=", ">", "<")                      // Short form
        val logicOperators = listOf("&&", "||", "!")
        val specialCharacters = listOf("(", ")", "{", "}", "=")

//        val allTokenTemplates = keywords + mathOperators + comparisonOperators + logicOperators + specialCharacters

        val tokens = mutableListOf<MutableList<Token>>()
        val lines = str.lines()

        // Breaking down lines into separate words and tokenizing them
        for (line in lines) {
            val lineSplit = line.split("\\s+".toRegex())
            val row = mutableListOf<Token>()
            for (token in lineSplit) {
                when {
                    keywords.contains(token) -> row.add(Keyword(token))
                    mathOperators.contains(token) -> row.add(MathOperator(token))
                    comparisonOperators.contains(token) -> row.add(ComparisonOperator(token))
                    logicOperators.contains(token) -> row.add(LogicOperator(token))
                    specialCharacters.contains(token) -> row.add(SpecialCharacter(token))

                    Regex("[a-zA-Z_]+").matches(token) -> row.add(Variable(token))
                    Regex("0|[1-9][0-9]*").matches(token) -> row.add(CustomInt(token))
                }
            }
            if (row.size > 0) {
//                row.add(EOL("\n"))            // Adds EOL character, currently not in use since rows are used instead
                tokens.add(row)
            }
        }

        return tokens
    }

    fun printTokensInRows(tokensInRows: MutableList<MutableList<Token>>) {
        tokensInRows.map { row ->
            row.map { token -> print("$token ") }
            println()
        }
    }
}

class Parser {
    fun parse(tokensInRows: MutableList<MutableList<Token>>): AST {
        val ast = AST(mutableListOf())
        val tokenIterator = tokensInRows.iterator()
        while (tokenIterator.hasNext()) {
            val nextRow = tokenIterator.next()
//            println(nextRow)
            when (nextRow.elementAt(0).toString()) {
                "if" -> {}
                "elif" -> {}
                "else" -> {}
                "when" -> {}
                else -> {
                    if (nextRow.elementAt(1).toString() == "=" && nextRow.size >= 3) {

                        ast.addCodeBlock(Assignment(
                            nextRow.elementAt(0).toString(),
                            Expression.getExpressionFromTokens(nextRow.slice(2 until nextRow.size).toMutableList()))
                        )
                    }
                }
            }

        }

        return ast
    }
}

class InputReader {
    // TODO: Implement
    fun getSourceFromFile(fileName: String): String {
        return ""
    }

    fun getSourceFromStringHard(): String {
        return """
            s = 0 + 1 + 2
            b = 3
            c_ = 45

            if ( s > b ) {
                print ( s )
            } elif ( s > 4 ) {
                print ( 5 )
            } else {
                print ( 6 )
            }

            while ( s < b ) {
                if b > s {
                    s = 7
                }
                s = s + 8
            }
        """.trimIndent()
    }

    fun getSourceFromStringSimple(): String {
        return """
            s = 0 + 1
        """.trimIndent()
    }

    fun getSourceFromStringSimpleDouble(): String {
        return """
            var = 0 + 1
            letters_ = 20 + 109 * 4
        """.trimIndent()
    }
}

class Compiler(val inputReader: InputReader, val lexer: Lexer, val parser: Parser)

// AST related classes

class Sign() {

}

class Term(val temp_text: String) {
    override fun toString(): String {
        return temp_text
    }
}

class Expression(val term: Term, val sign: Sign?, val expr: Expression?) {
    companion object {
        fun getExpressionFromTokens(tokens: MutableList<Token>): Expression {
            return Expression(Term(tokens.toString()), null, null)
        }
    }

    override fun toString(): String {
        return term.toString()
    }
}

class AST(val codeBlocks: MutableList<CodeBlock>) {
    fun addCodeBlock(codeBlock: CodeBlock) {
        codeBlocks.add(codeBlock)
    }

    override fun toString(): String {
        return "==== AST ====\nProgram\n\t$codeBlocks"
    }
}

abstract class CodeBlock {

}

class Assignment(val varName: String, val expr: Expression): CodeBlock() {
    override fun toString(): String {
        return " |$varName = $expr| "
    }
}

class If(): CodeBlock() {

}

class While: CodeBlock() {

}

fun main() {
    val compiler = Compiler(InputReader(), Lexer(), Parser())
    val input = compiler.inputReader.getSourceFromStringSimpleDouble()
    val tokensInRows = compiler.lexer.tokenizeInRows(input)

//    compiler.lexer.printTokensInRows(tokensInRows)

    val ast = compiler.parser.parse(tokensInRows)

    println(ast)

}