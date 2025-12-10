# Lobotomy Corporation Assembly - Example 2: Suppression and Panic/EGO
# Demonstrates: winst, wrepress, wattach, winsight, suppress, ego, panic, beq

.text
main:
    #### Clear registers ####
    sub $t0,$t0,$t0   # agent suppression power
    sub $t1,$t1,$t1   # abnormality power
    sub $t2,$t2,$t2   # temp for branch copy
    sub $s0,$s0,$s0   # department / misc

    #### Build agent power ####
    winst   $t0,$t0,15    # +15
    wrepress $t0,$t0,12   # +12  (total +27 from 0)

    #### Build abnormality difficulty ####
    wattach  $t1,$t1,8    # +8
    winsight $t1,$t1,10   # +10 (total +18 from 0)

    #### Attempt suppression with fixed risk (3) ####
    # Here we treat $t0 as agent, $t1 as abnormality
    suppress $t0,$t1,3

    # Result is stored in $v0 (register 2)
    add $t2,$v0,$zero     # t2 = v0 (0 = fail, 1 = success)

    #### Branch on success/failure ####
    beq $t2,$zero,fail    # if result == 0 → fail branch

success:
    # Successful suppression path: reward with E.G.O.
    ego $t0,3              # Print E.G.O. message for agent in $t0
    j end

fail:
    # Failed suppression: agent panics
    panic $t0              # Clear $t0 and print panic message
    j end

end:
    j end                  # idle loop
