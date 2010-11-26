#!/usr/bin/env ruby
require 'rubygems'
require 'zookeeper_client'

z = ZooKeeper.new("#{ARGV[0]}:2181")

if ARGV[1] == "n" then
  z.create("/app2", "parent node", 0)
  z.create("/app2/counter", "0", 0)
  puts "New Counter\n"
elsif ARGV[1] == "i" then
  result = 0
  ARGV[2].to_i.times {
    begin
      data, stat = z.get("/app2/counter")
      value = data.to_i
      value = value + 1
      data = value.to_s
      z.set("/app2/counter", data, stat.version)
      result = data
      retries = 0
    rescue
      retry
    end
  }
  puts "Item:#{result}\n"
else
  z.delete("/app2/counter", -1)
  z.delete("/app2", -1)
  puts "Deleted\n"
end
