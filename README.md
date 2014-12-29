Christmas
=========
A Java TUI ([text-based user interface](https://en.wikipedia.org/wiki/Text-based_user_interface)) widget toolkit which aims at using no native code at all (even on Windows platforms).

# Features

 * Support for common terminal types (`ansi`, `linux`, `xterm*`, `screen*`, `vt*`, `dtterm` etc.) and terminal emulators (XTerm, RXVT, PuTTY, DtTerm etc).
 * Support for 16 ANSI colours. Bright foreground is implemented using either `BOLD` attribute or `AIXTerm` control sequences (terminal-dependent). Bright background is implemented using either `BLINK` attribute (RXVT family) or `AIXTerm` control sequences (terminal-dependent).
 * Pseudographics via either Unicode line drawing characters (if locale's codepage supports it -- i. e. for `UTF-8`, `IBM866`, `KOI8-R`) or VT100 line drawing characters.
 * Continuous window size querying (for terminals which have this capability). Support for `SIGWINCH` (UNIX) is planned.
 * Correct handling of most control and escape sequences, incl. function keys (ANSI/Sun/SCO etc.)
 * Custom window titles supported. 
 * Own "database" of terminal capabilities

# Running

```bash
mvn exec:java
```

# Similar libraries

 * [`lanterna`](https://code.google.com/p/lanterna/) (also pure Java)
 * [Turbo Vision](http://tvision.sourceforge.net/)
 * [Turbo Vision rewritten in C#](http://www.codeproject.com/Articles/750873/Turbo-Vision-resurrected-for-Csharp-and-with-XAML)
 * [Textmode WINdow environment](http://sourceforge.net/projects/twin/)
 * [`ncurses`](https://www.gnu.org/software/ncurses/)
 
# Links

 * [XTerm Control Sequences](http://invisible-island.net/xterm/ctlseqs/ctlseqs.html)
 * Comparison of Terminal Emulators
     * [Title Support](http://unix-junkie.github.io/christmas/Comparison%20of%20Terminal%20Emulators%20-%20Title%20Support.html)
     * [Colour Support](http://unix-junkie.github.io/christmas/Comparison%20of%20Terminal%20Emulators%20-%20Colour%20Support.html)

# Screenshots

 * `cmd.exe` on Windows 7: ![cmd.exe on Windows 7](http://unix-junkie.github.io/christmas/cmd.png "cmd.exe on Windows 7")
 * PuTTY on Windows 7: ![PuTTY on Windows 7](http://unix-junkie.github.io/christmas/putty.png "PuTTY on Windows 7")
 * `xterm` on Linux: ![xterm on Linux](http://unix-junkie.github.io/christmas/xterm.png "xterm on Linux")
 * `mrxvt` on Linux: ![mrxvt on Linux](http://unix-junkie.github.io/christmas/mrxvt.png "mrxvt on Linux")
 * `urxvt` on Linux: ![urxvt on Linux](http://unix-junkie.github.io/christmas/urxvt.png "urxvt on Linux")
 * `rxvt-unicode` on Linux: ![rxvt-unicode](http://unix-junkie.github.io/christmas/rxvt-unicode.png "rxvt-unicode")
 * `gnome-terminal` on Linux: ![gnome-terminal](http://unix-junkie.github.io/christmas/gnome-terminal.png "gnome-terminal")
 * `xfce4-terminal` on Linux: ![xfce4-terminal](http://unix-junkie.github.io/christmas/xfce4-terminal.png "xfce4-terminal")
 * `konsole` on Linux: ![konsole on Linux](http://unix-junkie.github.io/christmas/konsole.png "konsole on Linux")
