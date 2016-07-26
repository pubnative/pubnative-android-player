package net.pubnative.player.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import net.pubnative.player.VASTParser;
import net.pubnative.player.VASTPlayer;
import net.pubnative.player.model.VASTModel;

public class MainActivity extends Activity implements VASTPlayer.Listener,
                                                      VASTParser.Listener {

    private static final String TAG = MainActivity.class.getName();
    private static final String VAST = "<VAST version=\"2.0\"><Ad id=\"128a6.44d74.46b3\"><InLine><AdTitle>SpotX Integration Test Secure</AdTitle><Description>SpotX Integration Test Secure</Description><Error>http://search.spotxchange.com/exception?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtu2zgQ1f6KPkDmVRca-5BeAnQRBwXS1HVfCEqkY3p18YpULkj77btDSnG76T4uDcgccubwzJkhWYZQkiTmsTEnb4c-MYU2tMpZrY3B2PBSFRjzfVUxRRBCNGkOyvbJMN6p3jY_BfIEM04xyStSJghzlBMcUPYLCvoXCoYfj9-k5LRiMLm9STjJKpQVNCNFUk-9bo1ohi6DIzV5sH32Nky2cDwmpcozxnTBMpbX9NcFQEOJNqoFZCWweLaCrpVg4tkJKtLa6nStBYL0MXo1CCpxnpcYEi8IzkuOKakYRRUtirxApCxRycHgaydykZ7U0zD5F7Rf4VhREVzQHBEKYaTiVQkyVQzToioZx4zQnHGEq6BewOQidSfT_z8EcQUMp7q1jYRaHVR_Z-TemHTmDvA5Qun6-_cg2PXt1VWyaARhkzOjvJuCUqAZAW_MWVlq9V9VvYQR_DBeAq2TvXmISyLF8R90b4bhT2ukHjroorBIANcZNTaHzJ0Gv1DMoOwvaGrS1vSNkTMRJFLg-8KVB67BqzaqGXrpn04xN6B_bs1gl6BCq5687eI2HIp5nACne6vNIO9Va7Xc29F5uR_V7BcPi2hKS_fkvAFasZ1iF42-DuaZRNMCUy8dAKZrK9A6et2fbDq33rwE9f388eLDu-CCZ5fNRxYsMluXV5-DRcFiIt2-f7OBjF_lAKFL_7nmYGa2Afji5hNZEoYyazmNbTALSOHg_cmJ1arR_aI1TEDo1YOpnfVmZXtv7kYVNJPeOL_qjLZqRaDI8i-6cqaZRvNlcyVx9ti1oWvmtOf6SWja5qwORuFmBB29Gd15Gfhqc28bE1boIqN59GcHEvACOTn7SXU6LVW9DiJAjt2gTRvMWMfBxR0Yc0_ET4LjuxbnCF6AG0D8MrdWkhS8YmTe-w32fkT9-ODkbxjz_Cf3Vx8F5z9HSvthCve1jvWEmmnl1TnD0Pchi_lFW9q4EClBFWEMLVcxeMxboEBcxAB0jpEH5Q7ni6hLZpRq4BmhudaKaZXXOaJ5SWt4eXG80IHit93xj-OG3KIN2aFd94F-PV48bt41-Ov2stsd2-Nue91dH3dst33_-z-Ec7ZQ&amp;beacon_type=exception&amp;exception%5Bid%5D=%24EXCEPTION_ID&amp;exception%5Bdata%5D=%24EXCEPTION_DATA&amp;exception%5Btrace%5D=%24EXCEPTION_TRACE&amp;syn%5Btiming%5D=%24TIMING_DATA</Error><AdSystem version=\"1.0\">PubNative</AdSystem><Impression>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_impression</Impression><Impression>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_start</Impression><Impression>http://um.simpli.fi/spotx_match</Impression><Impression>https://usersync.videoamp.com/usersync/spotx?USER_ID=015488dabdef11e58a7015f994a2FFFF</Impression><Impression>http://spx.sync.yume.com/tracker/dynamic_ytrack_sync?seat=i7CpbGLj&amp;redirect=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D7705%26uid%3D%24%7BUSER_ID%7D</Impression><Impression>http://ib.adnxs.com/getuid?http://sync.search.spotxchange.com/partner?adv_id= 7715&amp;uid=$UID</Impression><Impression>http://rtd.tubemogul.com/upi/pid/h0r58thg?redir=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6409%26uid%3D%24%7BUSER_ID%7D%26img%3D1</Impression><Impression>http://pm.w55c.net/ping_match.gif?ei=SPOTX&amp;rurl=http%3A%2F%2Fsync.search.spotxchange.com%2Fpartner%3Fadv_id%3D6465%26uid%3D_wfivefivec_%26img%3D1</Impression><Impression>http://ad.turn.com/r/cs?pid=16</Impression><Impression>http://sync.tidaltv.com/Spotx.ashx</Impression><Impression>http://log.adap.tv/spotx_sync</Impression><Impression>http://pix04.revsci.net/J13421/a3/0/3/0.302?matchId=spotx</Impression><Impression>http://p.rfihub.com/cm?in=1&amp;pub=935</Impression><Impression>http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVdtu4zYQVX9FHyDzKkoy-rDb3QAF4mDR3DZ5ESiRtunq4op0EiObb2-HpOzdZvtYBpCHw5nhmTMzDMsQSpKk0bIdh0QLpWmZs0ZpjbHmhRQY83VZMkkQQjRpt9IMyTht5GDakxdKMOMUk7wkRYIwRznBPsR6DoH-FQLDHw_fpOC0ZCDcXiecZCXKBM2ISJrDoDpdtWOfwX2KPJsh-80L93A3JoXMM8aUYBnLG_qzAqKhRGnZQWRZ4erVVHQpK1a92opWaWNUulQVgsQxercIKnCeFxiyFgTnBceUlIyikgqRC0SKAhUcNnxpq7xK9_I4Htwp2s_hmCgJFjRHhIIbKXlZAE0lw1SUBeOYEZozjnDp2fMxeZXavR7-H4C4BISHpjNtrV-AyGGj67XWacQO4XOE0uXbmyfs6vbyMpk5AreD1VO9OXimgDMC1pizolDyv6p6AcvbYTw7GlsP-jmoqhSHX-C9Hcc_ja7V2EMLeSWBuFbLqd1mdj-6GWIGZT9Fkwdl9NDqOgJBVQp4AabwMCGHTTc2skuXpkLLUFvTS7-jTAjEQCX8tYfBTcc0GhBCZ0l50yAJzGdOJr0x4zCrcyq8xCCEccdZJIyx2Xg_WucvP-l5xMY9Ng8-DkftjvtAuS-tk5PzcgHenTw604cj4CFCIHDtk1F6rJ9kZ1S9NpN19XqS0S7kH-ojVW2P1mlgKnR4ADy5xm_PANoOyHO1hYA_UPS0N2mchqgCXHdfPvz-yZvgaLL6wvyOxN3F5V3gNCZ6__njChJ9lwO4ziNh263uTwnffbi-IXPC0HmqPkyBMAEpbJ3b22qxaNUwlx8EqP3iWTfWOL0wg9ObSTrjSdTWLXqtjFwQ6Lv6L7qwuj1M-uvqssbZS9_5Ro5px5aqYY7aMzsY-WH1PDo92bMa8Cr9ZFrtNXSmUb-4swHx8Ty4OtrVcr-fK-r5eQtZ9qPS3YkwqOVo51NYcbDCJ8HhqQ0yvJjJNcT9Gns-SQQvGYlnv8DZd6_vH5z8DSvKP5i_-0hA8BpgraHxYWyaUFWonJJOnvP0A-lziU_tPF8wLQSV0MpobnFvEY-Ah6DEEOjsU2-l3Z5fCFUwLWUL7xvNlZJMybzJEc0L2sC_BBxeGg_x28PNR_N4_8du9anrHnYX_RV5NA83n9ljv3q52rX4cbfCq_uL3erm9td_ADtU3S4%7E&amp;beacon_type=start&amp;syn%5Btiming%5D=%24TIMING_DATA</Impression><Impression>http://flash.quantserve.com/pixel.swf?media=ad&amp;event=played&amp;title=spotxchange&amp;videoId=spotxchange&amp;pageURL=&amp;url=&amp;publisherId=p%2D04HdaWoZepDyg&amp;labels=85394</Impression><Impression>http://pixel.quantserve.com/seg/r;a=p-04HdaWoZepDyg;redirect=http://search.spotxchange.com/track/bt/1/img?segs=!qcsegs</Impression><Impression>http://tags.bluekai.com/site/363</Impression><Impression>http://loadr.exelator.com/load/?p=104&amp;g=420&amp;j=0</Impression><Impression>http://ad.crwdcntrl.net/5/pe=y/c=4914?http://search.spotxchange.com/track/bt/7/img?segs=${aud_ids}</Impression><Impression>http://adadvisor.net/adscores/g.pixel?sid=9201737408</Impression><Impression>http://b.scorecardresearch.com/b?c1=1&amp;c2=6272977&amp;c3=85394&amp;cv=1.3&amp;cj=1</Impression><Impression>http://t.pubnative.net/tracking/imp?aid=1007964&amp;t=KzWQMDAO8VVrkbzacfN31kNtqo4b_rJhJenjwQ1gOaT6CsAgBjN96XvJ9N6aICb40m4qrIptja7Wbfc4WkbI5vgIhTbWAXahMz8IYu6x9U67FIznDy_4LrHOXKi2T9jMZsm-Mpt0AJMTnzVE1iFM4hJMOzbsI8KGRfy2T2GLJDz0lIQANAs0OCVg-YLxSMSca6DNqRVEUnpmhNjpvjulbv-7kIZc6yLHpDeXm_x5Lzn2wgTXQUjFURZ7HnejIucs3hsf0N7LdYpQfqZBDxon-p1yjkyjoJAxqH2Iorhki8OcWQmCXpfqm1mYd1z1LG8a9wDXHeCZyDNg50Q3IEo3qJaEwacEsB10DTke44iA7viJ0z4nod0wa4Qw9Ic7cpsdUFA-5IkAggXRM58NetVL2q1-he-vKdRdAl-iMXrVmBQ</Impression><Creatives><Creative sequence=\"1\"><Linear><Duration>00:00:15</Duration><TrackingEvents><Tracking event=\"firstQuartile\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_firstQuartile</Tracking><Tracking event=\"midpoint\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_midpoint</Tracking><Tracking event=\"thirdQuartile\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_thirdQuartile</Tracking><Tracking event=\"complete\">https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_complete</Tracking><Tracking event=\"complete\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtS2zAQ1bf4AxLdLNvK9AGGwnQmSek0QOHFI1sKEePErqWkoZRfb7uSQ5jSPlYPzu76aHP2HMl8hDFCqDKqbjfIZNqwQvBKG0OISXOVEZIui4IrijFmqF4pu0Ftf682tn7ZRRDhKSNUFDRHmKRYUBJaLA8t8B8tAI3S-ER5ygoOwdVnoIARobkSI851xkdcVCzUMdJGNYBRksgnK9lESS6fnGQyqaxOJlpiGIHgN4vinAiRE-CfUSLylDBacIYLlmUiwzTPcZ5Ckk6cFDLp1GO79S_d_m7Hs4KSjAlMGWyjRVrkMHDBCcuKnKeEUyZ4ikkRdAg9U5m4zmz-D0FSAMNt1di6NHuwYHNvyqUxycAd2guMk8nzcxBsfjWdglhYPj2_xGnQixAQLPpV-scu7s1lUrfrrjH-Je0a9ejtOqZUJiSNAUi9s9q05U41VpdL2ztfLns14LBMwg9QVLp0j86bdTKJbkWTel-F9MihbqzZ-NJBw2RiJZ5E1K6zyeDsUAL5ri9PPpwFCBkgs0seMjpk59PrkDHIuExu3p_OQIA3M8DWg72uXpmBbWh88nlBDwODirrc9k1IMxhh5X3n5Hhc683Ida3fhwBEGn8zlbPejO3Gm_teeRt0NM6P10ZbNaZwxsuvbOxMve3Nl9m0JKP9ugmmDGM7o_p6VcKZqI_qEBwOXtDRm94dy8BXm52tTaiwg4xm748AGvoFcuWAK1XXHUydBxFgxnWrTRPS6GPr4htYw5GID0Ti1Y8xDrcRZWnBKUI_44LaK_b1QdAvWEM84P-FUvCvT5HIst2GS1BFF8Eprbw6zkVgsMC9AkxjynCdwQaZUFxQzvHhfAfE8ArmjkUCjY57ypVyq1BlMCnWOTdK1XA3mdBaca1EJTATOavgw0TiLQkUf8wfTu3s4tP32eJq__Hm3N4-3K3nFzM8u7nFt4sa393M2Hxxbu_O7t_9Bsk2cgM%7E&amp;beacon_type=complete</Tracking><Tracking event=\"firstQuartile\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVG1v2jAQzm_JDwC_xUmM9qFTu6kSsKpry-iXyIkNuAoJiw2Dsf71bWeHUq3bxzlSdHc-P7l7Hl_YAKEoikotq7aJdKo0zTkrldYY6ySTKcbJIs-ZJAghGlUraZqo7ZayMdXLKRJhllBMeE6yCOEEcYI9xOIEgf6AwPAk4R1lCc0ZGPefoQQUYZJJPmBMpWzAeEl9HEVKyxpypMDiaAQdScHE0Qoq4tKoeKQEghYwerMIyjDnGYb6U4J5lmBKckZRTtOUp4hkGcoScJKRFVzEG3lot-4F7W84luYEp5QjQuEYyZM8g4ZzhmmaZyzBjFDOEoRzz4PHTERsN7r5PwXiHCrclrWpCr0HCZqlLhZax33tAM8RikfPz56w6f14DGQhcXx-sRPPF8ZAWNCrcIdNOAuona62XWeapfcz-EotD86swzYRMU6CAVzvjNJtsZO1UcXCdNYVi072eUjEJzSpCnuwTq_jUZArqNS50rvnIqra6MYVFgDjkRFoFLJ2GxP30vYh4O_h5uL60qfgPmVyw7xHeu_D-MF7FDwm4tnV-wkw8KYHOHrS11Yr3VfrgS8-35FTw0CjKrZd7d0UWlg5t7FiOKxUM7Cb1u29UbXr4TddWuP00DROLzvpjCdSWzdca2XkkMAlL77SofWE6i-TcYEH-3XtVenbtlp21aqAS1Gd2cHI3zzPo9OdPYehXqV3ptI-Qk806r07JxCP54sr-rxCbjYnVaeeBOhx3Spdezfo2NqwA6u_E-EV4TD7wUZ-HKM0yRmJop9hQew19_WFo1-wervP_1eWhK8eQyGLduunoAwqglJKOnnuC0NjvvYScmpd-HkGGURMUE4YQ6cL7jP6Leg7BDEAnc8UK2lXPkqhU6QypqWsYDgpV0oyJXnJEeUZLeHPhMOY-BJ_zO8u6KeP9-xxffs0n92a6eXkMP0-h-d2_Ti7ZvPZw9Pk7urwOJu8-w3F-XMW&amp;beacon_type=recurring&amp;view_percent=25</Tracking><Tracking event=\"midpoint\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtu2zgQ1bfoA2wOb5Jo7EOLNECB2Gi3Sdrti0CJtM1dWXJFOuskza-3HVKOi832sTRAzAyHRzPncMxnhGRZ1ljdDn1mC2NZJXljrAWwotQFgFhXFdeUEMKydqtdnw3jRveufb7FMuCCAZUVLTMCgkgKEWJ9giD_gQD8ibRnpWAVR-PmA5ZAMqClljPOTcFnXDYsxklmrO4wRytQj06xhVZcPXrFVN44ky-MItgCkBeLkhKkLAHrLyjIUgCjFWekYkUhC0LLkpQCHbHwSqp8r--HQ3hG-z8cLyoKBZOEMrxGK1GV2HDFgRVVyQVwyiQXBKrIQ8QUKvd72_-eAqHCCg9N59raHlGCfmPrtbX5VDvCS0LyxdNTJGx1c3WFZBH1-PRsi8gXABKW9KrD_T7dRdTRtodxdP0m-iV-pdP3we3SMVU5iGQg13fO2KG-050z9dqNPtTrUU95ROUnNG1qf--D3eWLJFdSaQxNdM9FtJ2zfag9AuYLp8giZd3tXT5JO4WQv9t3r95exBSYUpbvePTo5F1e3UaPocdV_vHN6yUy8KIHvHrS17dbO1UbgV99uKanhpFGUx_GLroFtrANYe_VfN6afub3QzhGox12839t412wc9cHuxl1cJFI68N8Z43Tc4qPvP7C5j4Saj8tr2qYHXddVGVq21s9ttsaH0V7ZgdIfHmRx2BHfw5jvcbeudbGCDvRaI_hnEAjXiyunvJqvd-fVF1FErDH3WBsF92k4-DTCa7pTaQtgzT7ySZxHLNCVJxm2be0MPYz9-cG2Xdckz3l_ypL41cfUyHr4RCnoEkqolJGB33uC7CxWHuDOZ2t4zyjDCqnpKKck9MDjxnTEfadgoBA5zv1VvttjDLslJiSW61bHE4mjdHcaNlIwmTJGvxngjQmscSvq-s3YkXfs-XFa7eil91qd8OXfy-Pfz28hc_Xl271cLv9vPvzn9XD5o8fsRxymg%7E%7E&amp;beacon_type=recurring&amp;view_percent=50</Tracking><Tracking event=\"thirdQuartile\">http://search.spotxchange.com/beacon?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNtu2zAM1bf4AxLdLNsK9tChLTCgCbqtl7UvhmwpjTpfMktpm2X99W2UnKZYt8cpgCBS5DF5jhg-wRghVBlV9x0ymTasELzSxhBi0lxlhKTLouCKYowZqlfKdqgf7lRn65csjghPGaGioDnCJMWCkgCx3EPgPyAI_NK4ozxlBSSjy89QAkaE5kpMONcZn3BRseDHSBvVQIySRO6sZDMludw5yWRSWZ3MtMTQAsFvFsU5ESInUH9GichTwmjBGS5YlokM0zzHeQpGOnNSyGSttv3Gv6D9DcezgpKMCUwZpNEiLXJouOCEZUXOU8IpEzzFpAg8BMxUJm5tuv9TICmgwk3V2Lo0TyBBd2fKpTHJWDvAC4yT2fNzIGxxeXYGZGG5e345p4EvQoCwqFfpt-uYC6iDqTfDYLu7YOfwlUZtvW3jNZUJSeMBuH6w2vTlg2qsLpd2cL5cDmqMwzLZoylduq3zpk1mUa6o0uCrYB6KqBtrOl86AExmVuJZjHpY22SUdnQBf1fnRx-OQwgZQ-bnPFh0tE7ProLFwOIyuT55PwcG3vQAqXt9Xb0yY7UB-OjzBd03DDTqcjM0wcyghZX3ayen01p3E7fu_VM41H07fTSVs95MbefN3aC8DUQa56et0VZNKTzy8hubukCo-TI_K8nkqW2CKmPbzqihXpXwKOoDOwSHlxd49GZwBzfUq82DrU3wsD2N5skfAmjAC8WVY1yp1uu9qotAAvTY9to0wYw69i7ewBrfRNwQibMfzziMI8rSglOEfsYFvtfY142gX7DG8xj_rygFX93FQpb9JkxBFVUEpbTy6tAXgcZC7RXENKYM8wwyyITignKO9w88RIxX0Hd0EgA65JQr5VbBy6BTrHNulKphOJnQWnGtRCUwEzmr4J-JxDEJJf5YHJ9-XVzUj_P2I765vmpv28vt_L7-fnOxul_QE357DP77T83i_ujdb9kncxE%7E&amp;beacon_type=recurring&amp;view_percent=75</Tracking></TrackingEvents><VideoClicks><ClickThrough>http://search.spotxchange.com/click?_a=85394&amp;_p=128a6.44d74.46b3&amp;_z=1&amp;_x=eNqtVNty0zAQFb_iD3C1uksZnig8MBCGoQXaF49sqYnTxM7EDmmm9NthZbsZSHlEmVHWq92jo7MriZxSQki1rqt7EnWI3CpRhhgBojReA8g7a4VnlFJOqqWvG9LuFr6pqymJEhCSA1OWGUJBUsUgIdxNCPQvBMCfHGZiJLcCjesvRLLc0lzznGlS7puwjq5qNzluF9ihbvI3yfiGWwMzXuVCBC1yoUr-0oFolITo14jsHbjH2vGZd8I9do67rKxDNguO4rGBng1GDShlAA-tGSgjgTMrOLVca6UpM4YaiR9y1jnlsq0_tvv-Ge0lnNCWgeaKMo5pzEprUCYrgGtrhATBuBKSgk3qJUzpsm4bm_9DECwy3JdYoiI-oJDNIhZ3MWYjd4RXlGazp6ck2Pz6wwcyaYRp-y7uisU-KYWaMYwGKYwJ_l9VfYcjxQFMiXVXNPEwuFwGwz_qXrXtfR2L0G6wg5KTIW4X_a5a5t227SeKOZb9Gc3vQx2bKhYjEeoy5Is0daKJZ1is29Kvs1nt6Gyobb3x6YsLralAl07b7pt-d8zGAMb4ZIUUOlga5KTJLi7qtpnciutkCYSo--NkMiHEFLxtuz5t_uyXIzdI3BL58WgF1rPKZs9-mppm5zexj7vu5Ea0EH_UVUwePvVpfOhPASzhJY2KMa7w223RH7cxnZbNnobO2bQhrkcHZris7aZVHGTZ99vOXVwcDodztS_G6hNID0E0kHrR_-sBSIUeY_HGky-I8n1EIURLK9i49grXBuNsAvILx2j_EX42eWT-OBznDguHZS8djBIH3_uTPqmhkgbjUzH1B1abUYuloFOJUsS4hPoNTkCgU06x9N3y1OHBiOh9hfeTqxC8CF6VinJleIlPGgw3JVH8Ob98f3-7mq8_Xr09ztnb483mM71ZLcT8crmcr25Xny6vHz5dfV3dXl2__g2scXgO</ClickThrough><ClickTracking>https://cdn.spotxcdn.com/website/integration_test/beacons/tracking.html?vast_VideoClickTracking</ClickTracking></VideoClicks><MediaFiles><MediaFile delivery=\"progressive\" type=\"video/mp4\" bitrate=\"2461\" width=\"960\" height=\"540\">https://cdn.spotxcdn.com/website/integration_test/media/2015_q3/Spotx_red.mp4</MediaFile></MediaFiles></Linear></Creative></Creatives><Extensions><Extension type=\"PN-Postview-Banner\"><Banner id=\"2\">http://cdn.pubnative.net/static/custom_preview_banners/uplike2.jpg</Banner></Extension></Extensions></InLine></Ad></VAST>";
    private VASTPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player = (VASTPlayer) findViewById(R.id.player);
        player.setListener(this);
    }

    public void onLoadClick(View view) {

        Log.v(TAG, "onLoadClick");

        new VASTParser(this).setListener(this)
                            .execute(VAST);
    }

    public void onPlayClick(View view) {

        player.play();
    }

    public void onStopClick(View view) {

        player.stop();
    }

    // VASTParser.Listener
    //---------------------------
    @Override
    public void onVASTParserError(int error) {

        Log.v(TAG, "VASTParser.Listener.onVASTParserError: " + error);
    }

    @Override
    public void onVASTParserFinished(VASTModel model) {

        Log.v(TAG, "VASTParser.Listener.onVASTParserFinished");
        player.load(model);
    }

    // VASTPlayer.Listener
    //---------------------------

    @Override
    public void onVASTPlayerLoadFinish() {

        Log.v(TAG, "onVASTPlayerLoadFinish");
    }

    @Override
    public void onVASTPlayerFail(Exception exception) {

        Log.v(TAG, "onVASTPlayerFail");
    }

    @Override
    public void onVASTPlayerPlaybackStart() {

        Log.v(TAG, "onVASTPlayerPlaybackStart");
    }

    @Override
    public void onVASTPlayerPlaybackFinish() {

        Log.v(TAG, "onVASTPlayerPlaybackFinish");
    }

    @Override
    public void onVASTPlayerOpenOffer() {

        Log.v(TAG, "onVASTPlayerOpenOffer");
    }
}
