interfaces {
    ethernet eth0 {
        address 172.17.0.2/16
        ipv6 {
            address {
                no-default-link-local
            }
        }
    }
    ethernet eth1 {
        address 10.0.0.254/24
        ipv6 {
            address {
                no-default-link-local
            }
        }
    }
}
service {
    ssh {
    }
}
system {
    config-management {
        commit-revisions 100
    }
    conntrack {
        modules {
            ftp
            h323
            nfs
            pptp
            sip
            sqlnet
            tftp
        }
    }
    console {
        device ttyS0 {
            speed 115200
        }
    }
    host-name vyos
    login {
        user vyos {
            authentication {
                plaintext-password "vyos"
            }
        }
    }
    ntp {
        server time1.vyos.net {
        }
        server time2.vyos.net {
        }
        server time3.vyos.net {
        }
    }
    syslog {
        global {
            facility all {
                level info
            }
            facility protocols {
                level debug
            }
        }
    }
}
