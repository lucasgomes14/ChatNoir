package com.chatnoir.game

import java.util.PriorityQueue

object GameLogic {

    enum class MoveResult { MOVED, CAT_WON, USER_WON }

    // Direções tipo "hex" em grade quadrada para simplificar (6 vizinhos)
    private val dirs = arrayOf(
        intArrayOf(-1, 0), // cima
        intArrayOf(1, 0),  // baixo
        intArrayOf(0, -1), // esquerda
        intArrayOf(0, 1),  // direita
        intArrayOf(-1, 1), // diag cima-direita
        intArrayOf(1, -1)  // diag baixo-esquerda
    )

    private data class Node(
        val r: Int,
        val c: Int,
        val g: Int,            // custo até aqui
        val h: Int,            // heurística
        val parent: Node?
    ) : Comparable<Node> {
        val f: Int get() = g + h
        override fun compareTo(other: Node): Int = this.f - other.f
    }

    private fun heuristicToEdge(r: Int, c: Int, size: Int): Int {
        // Distância mínima até qualquer borda (admissível)
        val toTop = r
        val toBottom = size - 1 - r
        val toLeft = c
        val toRight = size - 1 - c
        return minOf(toTop, toBottom, toLeft, toRight)
    }

    private fun isEdge(r: Int, c: Int, size: Int): Boolean =
        (r == 0 || c == 0 || r == size - 1 || c == size - 1)

    private fun reconstructPath(end: Node): List<Pair<Int, Int>> {
        val path = mutableListOf<Pair<Int, Int>>()
        var cur: Node? = end
        while (cur != null) {
            path.add(cur.r to cur.c)
            cur = cur.parent
        }
        return path.reversed()
    }

    private fun aStar(board: Board): List<Pair<Int, Int>>? {
        val size = board.size
        val startR = board.catRow
        val startC = board.catCol

        val open = PriorityQueue<Node>()
        val inOpen = HashMap<Pair<Int, Int>, Node>()
        val closed = HashSet<Pair<Int, Int>>()

        val startNode = Node(startR, startC, g = 0, h = heuristicToEdge(startR, startC, size), parent = null)
        open.add(startNode)
        inOpen[startR to startC] = startNode

        while (open.isNotEmpty()) {
            val current = open.poll()
            inOpen.remove(current.r to current.c)

            if (isEdge(current.r, current.c, size)) {
                return reconstructPath(current)
            }

            closed.add(current.r to current.c)

            for (d in dirs) {
                val nr = current.r + d[0]
                val nc = current.c + d[1]
                if (nr !in 0 until size || nc !in 0 until size) continue
                val cell = board.grid[nr][nc]
                if (cell.type != CellType.EMPTY) continue // bloqueado
                if ((nr to nc) in closed) continue

                val tentativeG = current.g + 1
                val h = heuristicToEdge(nr, nc, size)
                val neighborInOpen = inOpen[nr to nc]

                if (neighborInOpen == null || tentativeG < neighborInOpen.g) {
                    val node = Node(nr, nc, tentativeG, h, current)
                    open.add(node)
                    inOpen[nr to nc] = node
                }
            }
        }
        return null // sem caminho -> gato preso
    }

    /** Realiza o turno do gato usando A* e retorna o resultado. */
    fun performCatMove(board: Board): MoveResult {
        val path = aStar(board)
        if (path == null) {
            return MoveResult.USER_WON // gato preso
        }
        // path[0] é a posição atual do gato; se já está na borda, ganhou
        if (path.size == 1) {
            return if (isEdge(board.catRow, board.catCol, board.size)) MoveResult.CAT_WON else MoveResult.MOVED
        }
        val next = path[1]
        board.moveCatTo(next.first, next.second)
        return if (isEdge(next.first, next.second, board.size)) MoveResult.CAT_WON else MoveResult.MOVED
    }
}