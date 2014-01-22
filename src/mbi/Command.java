package mbi;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public interface Command {
	String execute() throws MbiException, IOException;
}
