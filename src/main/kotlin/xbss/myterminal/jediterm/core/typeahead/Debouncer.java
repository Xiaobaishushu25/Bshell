package xbss.myterminal.jediterm.core.typeahead;

public interface Debouncer {
  void call();

  void terminateCall();
}