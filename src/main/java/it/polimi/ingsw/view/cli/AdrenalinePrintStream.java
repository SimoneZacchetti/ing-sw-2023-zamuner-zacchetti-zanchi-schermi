package it.polimi.ingsw.view.cli;

import java.io.PrintStream;

class AdrenalinePrintStream extends PrintStream {
    AdrenalinePrintStream() {
        super(System.out, true);
    }
}
