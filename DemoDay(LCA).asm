.text
main:

    # --- Morning: agent training (work types) ---
    # All four work types run once and print their messages.
    winst    $t1,$t2,15      # Fortitude training
    winsight $t1,$t2,10      # Prudence training
    wattach  $t1,$t2,8       # Temperance training
    wrepress $t1,$t2,12      # Justice training

    # --- Morning work result: extract PE boxes ---
    # Uses $t2 as base "Abnormality output" and writes result into $t1.
    extract  $t1,$t2,1       # ZAYIN/TETH-level extraction

    # --- Midday: small breach & suppression attempt ---
    # Suppression uses risk level 3 (WAW) and writes success (1/0) into $v0.
    suppress $t1,$t2,3

    # --- Noon: Ordeal event ---
    # Ordeal 0 damages one random $t register and prints which one was hit.
    ordeal 0

    # --- Afternoon: new E.G.O equipment ---
    # E.G.O id 3 applies a +5 bonus to $t1 and prints a message.
    ego $t1,3

    # --- Late day: panic incident ---
    # Clerk in $t1 panics; register is cleared and a message is printed.
    panic $t1

    # --- End of day: facility meltdown ---
    # Meltdown uses $s1 as the department register, prints meltdown message,
    # penalizes all agents in $t0–$t7, and increases global risk in memory.
    meltdown $s1

end:
    # Stay here so the simulator does not run off into empty memory
    j end
