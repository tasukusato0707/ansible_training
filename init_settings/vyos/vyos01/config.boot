high-availability {
    vrrp {
        group service_nw01 {
            interface eth1
            priority 150
            virtual-address 192.168.1.254/24 {
            }
            vrid 10
        }
        group service_nw02 {
            interface eth2
            priority 150
            virtual-address 192.168.2.254/24 {
            }
            vrid 20
        }
    }
}
interfaces {
    ethernet eth1 {
        address 192.168.1.252/24
        ipv6 {
            address {
                no-default-link-local
            }
        }
    }
    ethernet eth2 {
        address 192.168.2.252/24
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
